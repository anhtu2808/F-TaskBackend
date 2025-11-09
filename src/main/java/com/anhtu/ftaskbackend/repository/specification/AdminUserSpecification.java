package com.anhtu.ftaskbackend.repository.specification;

import com.anhtu.ftaskbackend.dto.request.admin.AdminUserFilterRequest;
import com.anhtu.ftaskbackend.entity.User;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminUserSpecification {

    public static Specification<User> filter(AdminUserFilterRequest params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Full name filter
            if (params.getFullName() != null && !params.getFullName().isBlank()) {
                Expression<String> fullName = cb.lower(root.get("fullName"));
                predicates.add(cb.like(fullName, "%" + params.getFullName().toLowerCase() + "%"));
            }

            // Phone filter
            if (params.getPhone() != null && !params.getPhone().isBlank()) {
                predicates.add(cb.like(root.get("phone"), "%" + params.getPhone() + "%"));
            }

            // Email filter
            if (params.getEmail() != null && !params.getEmail().isBlank()) {
                Expression<String> email = cb.lower(root.get("email"));
                predicates.add(cb.like(email, "%" + params.getEmail().toLowerCase() + "%"));
            }

            // Username filter
            if (params.getUsername() != null && !params.getUsername().isBlank()) {
                Expression<String> username = cb.lower(root.get("username"));
                predicates.add(cb.like(username, "%" + params.getUsername().toLowerCase() + "%"));
            }

            // Active status filter
            if (params.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), params.getIsActive()));
            }

            // Gender filter
            if (params.getGender() != null) {
                predicates.add(cb.equal(root.get("gender"), params.getGender()));
            }

            // Role name filter
            if (params.getRoleName() != null && !params.getRoleName().isBlank()) {
                Expression<String> roleName = cb.lower(root.get("role").get("name"));
                predicates.add(cb.like(roleName, "%" + params.getRoleName().toLowerCase() + "%"));
            }

            // Created date range filter
            if (params.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), params.getCreatedFrom()));
            }
            if (params.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), params.getCreatedTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
