package com.anhtu.ftaskbackend.repository.specification;

import com.anhtu.ftaskbackend.dto.request.admin.AdminServiceCatalogFilterRequest;
import com.anhtu.ftaskbackend.entity.ServiceCatalog;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminServiceCatalogSpecification {

    public static Specification<ServiceCatalog> filter(AdminServiceCatalogFilterRequest params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Name filter
            if (params.getName() != null && !params.getName().isBlank()) {
                Expression<String> name = cb.lower(root.get("name"));
                predicates.add(cb.like(name, "%" + params.getName().toLowerCase() + "%"));
            }

            // Active status filter
            if (params.getIsActive() != null) {
                predicates.add(cb.equal(root.get("isActive"), params.getIsActive()));
            }

            // Platform fee percentage range filter
            if (params.getMinPlatformFeePercent() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("platformFeePercent"), params.getMinPlatformFeePercent()));
            }
            if (params.getMaxPlatformFeePercent() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("platformFeePercent"), params.getMaxPlatformFeePercent()));
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
