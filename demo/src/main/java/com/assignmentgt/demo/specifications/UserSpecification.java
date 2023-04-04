package com.assignmentgt.demo.specifications;

import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import com.assignmentgt.demo.model.User;

public class UserSpecification implements Specification<User> {
    private final Double minSalary;
    private final Double maxSalary;

    public UserSpecification(Double minSalary, Double maxSalary) {
        this.minSalary = minSalary;
        this.maxSalary = maxSalary;
    }

    @Override
    public Predicate toPredicate(Root<User> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        Predicate minSalaryPredicate = criteriaBuilder.greaterThanOrEqualTo(root.get("salary"), minSalary);
        Predicate maxSalaryPredicate = criteriaBuilder.lessThanOrEqualTo(root.get("salary"), maxSalary);

        return criteriaBuilder.and(minSalaryPredicate, maxSalaryPredicate);
    }
}
