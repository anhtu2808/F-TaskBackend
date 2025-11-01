package com.anhtu.ftaskbackend.repository.specification;

import com.anhtu.ftaskbackend.dto.request.booking.FilterBooking;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class BookingSpecification {

    public static Specification<Booking> filter(FilterBooking params) {
        return (root, query, cb) -> {
            LocalDateTime from = params.getFromDate() != null
                    ? params.getFromDate().toLocalDateTime()
                    : null;
            LocalDateTime to = params.getToDate() != null
                    ? params.getToDate().toLocalDateTime()
                    : null;
            List<Predicate> predicates = new ArrayList<>();

            if (params.getStatus() != null)
                predicates.add(cb.equal(root.get("status"), params.getStatus()));

            if (from != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("startAt"), from));
            if (to != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("startAt"), to));

            if (params.getMinPrice() != null)
                predicates.add(cb.greaterThanOrEqualTo(root.get("totalPrice"), params.getMinPrice()));
            if (params.getMaxPrice() != null)
                predicates.add(cb.lessThanOrEqualTo(root.get("totalPrice"), params.getMaxPrice()));

            if (params.getAddress() != null && !params.getAddress().isBlank()) {
                var joinAddress = root.join("address");
                Expression<String> districtAddress = cb.concat(
                        cb.concat(cb.lower(joinAddress.get("addressLine")), " "),
                        cb.concat(cb.lower(joinAddress.get("district")), " ")
                );
                Expression<String> fullAddress = cb.concat(
                        districtAddress,
                        cb.lower(joinAddress.get("city"))
                );
                predicates.add(cb.like(cb.lower(fullAddress),
                        "%" + params.getAddress().toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
