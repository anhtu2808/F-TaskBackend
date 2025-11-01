package com.anhtu.ftaskbackend.repository.specification;

import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.dto.request.servicevariant.FilterServiceVariant;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.ServiceCatalogVariant;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ServiceCatalogVariantSpecification {

    public static Specification<ServiceCatalogVariant> filter(FilterServiceVariant params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (params.getServiceCatalogId() != null)
                predicates.add(cb.equal(root.get("serviceCatalog").get("id"), params.getServiceCatalogId()));
            if (params.getName() != null)
                predicates.add(cb.equal(root.get("name"), "%" + params.getName().toLowerCase() + "%"));
            if (params.getMinPrice() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("pricePerVariant"), params.getMinPrice()));
            if (params.getMaxPrice() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("pricePerVariant"), params.getMaxPrice()));
            if (params.getDurationHours() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("durationHours"), params.getDurationHours()));
            if (params.getIsMultiPartner() != null)
                predicates.add(cb.equal(root.get("isMultiPartner"), params.getIsMultiPartner()));
            if (params.getNumberOfPartners() != null)
                predicates.add(cb.equal(root.get("numberOfPartners"), params.getNumberOfPartners()));
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
