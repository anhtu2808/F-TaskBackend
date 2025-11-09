package com.anhtu.ftaskbackend.repository.specification;

import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerFilterRequest;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.entity.District;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminPartnerSpecification {

    public static Specification<Partner> filter(AdminPartnerFilterRequest params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Partner name filter (from user.fullName)
            if (params.getPartnerName() != null && !params.getPartnerName().isBlank()) {
                Expression<String> partnerName = cb.lower(root.get("user").get("fullName"));
                predicates.add(cb.like(partnerName, "%" + params.getPartnerName().toLowerCase() + "%"));
            }

            // Phone filter (from user.phone)
            if (params.getPhone() != null && !params.getPhone().isBlank()) {
                predicates.add(cb.like(root.get("user").get("phone"), "%" + params.getPhone() + "%"));
            }

            // Email filter (from user.email)
            if (params.getEmail() != null && !params.getEmail().isBlank()) {
                Expression<String> email = cb.lower(root.get("user").get("email"));
                predicates.add(cb.like(email, "%" + params.getEmail().toLowerCase() + "%"));
            }

            // Available status filter
            if (params.getIsAvailable() != null) {
                predicates.add(cb.equal(root.get("isAvailable"), params.getIsAvailable()));
            }

            // Active status filter (from user.isActive)
            if (params.getIsActive() != null) {
                predicates.add(cb.equal(root.get("user").get("isActive"), params.getIsActive()));
            }

            // District filter
            if (params.getDistrict() != null && !params.getDistrict().isBlank()) {
                Join<Partner, District> districtJoin = root.join("districts");
                Expression<String> districtName = cb.lower(districtJoin.get("name"));
                predicates.add(cb.like(districtName, "%" + params.getDistrict().toLowerCase() + "%"));
            }

            // Created date range filter
            if (params.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createAt"), params.getCreatedFrom()));
            }
            if (params.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createAt"), params.getCreatedTo()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
