package com.assignmentgt.demo.controller;

import com.assignmentgt.demo.model.User;
import com.assignmentgt.demo.repository.*;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.scheduling.annotation.Async;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.util.Collections;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.ArrayList;

@RestController
@RequestMapping("/api")
public class UserController {


    @Autowired
    private UsersRepository usersRepository;
    
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> getUsers(
            @RequestParam(defaultValue = "0.0") BigDecimal min,
            @RequestParam(defaultValue = "4000.0") BigDecimal max,
            @RequestParam(defaultValue = "0") Integer offset,
            @RequestParam(defaultValue = "2147483647") Integer limit, // Set the default limit to Integer.MAX_VALUE
            @RequestParam(defaultValue = "") String sort) {
    
    // Validate the 'sort' parameter
    String sortUpper = sort.toUpperCase();
    if (!sortUpper.isEmpty() && !("NAME".equals(sortUpper) || "SALARY".equals(sortUpper))) {
        return ResponseEntity.badRequest().body(Collections.singletonMap("error", "Invalid sort parameter. Allowed values are 'NAME' or 'SALARY'."));
    }

        Pageable pageable = PageRequest.of(offset / (limit == 0 ? 1 : limit), limit);
    
        List<User> users = usersRepository.findBySalaryRangeAndSort(min, max, sort.toUpperCase(), pageable);
    
        Map<String, Object> response = new HashMap<>();
        response.put("results", users.stream()
                .map(user -> new LinkedHashMap<String, Object>() {{
                    put("name", user.getName());
                    put("salary", user.getSalary());
                }})
                .collect(Collectors.toList()));
    
        return ResponseEntity.ok(response);
    }

    @Async
    @PostMapping("/upload")
    public CompletableFuture<ResponseEntity<?>> handleFileUpload(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
    
        try {
            // 1st pass: Validate the entire CSV file
            List<String[]> parsedRows = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8));
            String line;
            boolean firstRow = true;
    
            while ((line = reader.readLine()) != null) {
                String[] columns = line.split(",");
                if (firstRow) {
                    firstRow = false;
    
                    if (columns.length != 2 ||
                        !"NAME".equalsIgnoreCase(columns[0].trim()) ||
                        !"SALARY".equalsIgnoreCase(columns[1].trim())) {
                        throw new IllegalArgumentException("Invalid CSV format: header row is incorrect");
                    }
                    continue;
                }
    
                if (columns.length != 2) {
                    throw new IllegalArgumentException("Invalid CSV format: incorrect number of columns");
                }
    
                String name = columns[0].trim();
                double salary;
    
                try {
                    salary = Double.parseDouble(columns[1].trim());
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid CSV format: salary cannot be parsed");
                }
    
                if (salary >= 0.0) {
                    parsedRows.add(new String[]{name, String.valueOf(salary)});
                }
            }
    
            // 2nd pass: Insert records into the database
            for (String[] row : parsedRows) {
                String name = row[0];
                double salary = Double.parseDouble(row[1]);
    
                Optional<User> existingUser = usersRepository.findAll().stream()
                        .filter(user -> user.getName().equals(name))
                        .findFirst();
                User user = existingUser.orElse(new User(name, salary));
                user.setSalary(salary);
                usersRepository.save(user);
            }
    
            response.put("success", 1);
            return CompletableFuture.completedFuture(ResponseEntity.ok(response));
        } catch (Exception e) {
            response.put("error", e.getMessage());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

}
