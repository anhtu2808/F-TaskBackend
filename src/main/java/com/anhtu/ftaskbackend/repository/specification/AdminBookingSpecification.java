package com.anhtu.ftaskbackend.repository.specification;

import com.anhtu.ftaskbackend.dto.request.admin.AdminBookingFilterRequest;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.BookingPartner;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AdminBookingSpecification {

    public static Specification<Booking> filter(AdminBookingFilterRequest params) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Status filter
            if (params.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), params.getStatus()));
            }

            // Customer ID filter
            if (params.getCustomerId() != null) {
                predicates.add(cb.equal(root.get("customer").get("id"), params.getCustomerId()));
            }

            // Partner ID filter
            if (params.getPartnerId() != null) {
                Join<Booking, BookingPartner> bookingPartnerJoin = root.join("partners");
                predicates.add(cb.equal(bookingPartnerJoin.get("partner").get("id"), params.getPartnerId()));
            }

            // Service catalog ID filter
            if (params.getServiceCatalogId() != null) {
                predicates.add(cb.equal(root.get("variant").get("serviceCatalog").get("id"), params.getServiceCatalogId()));
            }

            // Variant ID filter
            if (params.getVariantId() != null) {
                predicates.add(cb.equal(root.get("variant").get("id"), params.getVariantId()));
            }

            // Customer name filter
            if (params.getCustomerName() != null && !params.getCustomerName().isBlank()) {
                Expression<String> customerFullName = cb.lower(root.get("customer").get("user").get("fullName"));
                predicates.add(cb.like(customerFullName, "%" + params.getCustomerName().toLowerCase() + "%"));
            }

            // Partner name filter
            if (params.getPartnerName() != null && !params.getPartnerName().isBlank()) {
                Join<Booking, BookingPartner> bookingPartnerJoin = root.join("partners");
                Expression<String> partnerFullName = cb.lower(bookingPartnerJoin.get("partner").get("user").get("fullName"));
                predicates.add(cb.like(partnerFullName, "%" + params.getPartnerName().toLowerCase() + "%"));
            }

            // Service name filter
            if (params.getServiceName() != null && !params.getServiceName().isBlank()) {
                Expression<String> serviceName = cb.lower(root.get("variant").get("serviceCatalog").get("name"));
                predicates.add(cb.like(serviceName, "%" + params.getServiceName().toLowerCase() + "%"));
            }

            // Start date range filter
            if (params.getStartDateFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("startAt"), params.getStartDateFrom()));
            }
            if (params.getStartDateTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("startAt"), params.getStartDateTo()));
            }

            // Created date range filter
            if (params.getCreatedFrom() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createAt"), params.getCreatedFrom()));
            }
            if (params.getCreatedTo() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createAt"), params.getCreatedTo()));
            }

            // Price range filter
            if (params.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalPrice"), params.getMinPrice()));
            }
            if (params.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("totalPrice"), params.getMaxPrice()));
            }

            // District filter
            if (params.getDistrict() != null && !params.getDistrict().isBlank()) {
                Expression<String> district = cb.lower(root.get("address").get("district"));
                predicates.add(cb.like(district, "%" + params.getDistrict().toLowerCase() + "%"));
            }

            // Customer accepted filter
            if (params.getIsCustomerAccepted() != null) {
                predicates.add(cb.equal(root.get("isCustomerAccepted"), params.getIsCustomerAccepted()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
