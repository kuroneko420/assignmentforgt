package com.assignmentgt.demo.controller;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.beans.factory.annotation.Autowired;
import com.assignmentgt.demo.model.User;
import java.util.stream.StreamSupport;
import com.assignmentgt.demo.repository.*;
import com.assignmentgt.demo.specifications.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import java.util.Map;
import java.util.HashMap;
import org.springframework.http.ResponseEntity;
import java.util.Arrays;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.util.LinkedHashMap;
import java.math.BigDecimal;
import java.util.Collections;

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

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgumentException(IllegalArgumentException ex) {
        Map<String, String> errorMap = new HashMap<>();
        errorMap.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorMap);
    }

}
