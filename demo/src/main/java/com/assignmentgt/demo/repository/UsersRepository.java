package com.assignmentgt.demo.repository;
import com.assignmentgt.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;

public interface UsersRepository extends JpaRepository<User, Integer> {
    @Query("SELECT u FROM User u WHERE u.salary BETWEEN :min AND :max ORDER BY CASE WHEN :sort = 'NAME' THEN u.name WHEN :sort = 'SALARY' THEN u.salary END ASC")
List<User> findBySalaryRangeAndSort(
        @Param("min") BigDecimal min,
        @Param("max") BigDecimal max,
        @Param("sort") String sort,
        Pageable pageable);
}
