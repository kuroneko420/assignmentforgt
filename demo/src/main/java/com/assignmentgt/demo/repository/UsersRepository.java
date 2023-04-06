package com.assignmentgt.demo.repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.assignmentgt.demo.model.User;

@Repository
public interface UsersRepository extends JpaRepository<User, Long> {

    @Query(value = "SELECT * FROM users u WHERE u.salary >= :minSalary AND u.salary <= :maxSalary " +
            "ORDER BY " +
            "CASE WHEN :sort = 'NAME' THEN u.name END ASC NULLS LAST, " +
            "CASE WHEN :sort = 'SALARY' THEN u.salary END ASC NULLS LAST, " +
            "u.id ASC " +
            "LIMIT :limit OFFSET :offset", nativeQuery = true)
    List<User> findBySalaryRangeAndSort(@Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("sort") String sort,
            @Param("offset") Integer offset,
            @Param("limit") Integer limit);

    @Query(value = "SELECT * FROM users u WHERE u.salary >= :minSalary AND u.salary <= :maxSalary " +
            "ORDER BY " +
            "CASE WHEN :sort = 'NAME' THEN u.name END ASC NULLS LAST, " +
            "CASE WHEN :sort = 'SALARY' THEN u.salary END ASC NULLS LAST, " +
            "u.id ASC " +
            "OFFSET :offset", nativeQuery = true)
    List<User> findBySalaryRangeAndSortWithoutLimit(@Param("minSalary") BigDecimal minSalary,
            @Param("maxSalary") BigDecimal maxSalary,
            @Param("sort") String sort,
            @Param("offset") Integer offset);
}
