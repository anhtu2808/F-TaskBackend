package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE booking SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Booking extends AbstractAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "customer_id", referencedColumnName = "id")
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "variant_id", referencedColumnName = "id")
    ServiceCatalogVariant variant;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id")
    Address address;

    @Column(nullable = false)
    LocalDateTime startTime;

    @Column(nullable = false)
    Double totalPrice;

    @Column(nullable = false)
    Double platformFee;

    @Column(columnDefinition = "TEXT")
    String customerNote;

    @Column(nullable = false) @Builder.Default
    Integer requiredPartners = 1;

    @Column(nullable = false) @Builder.Default
    BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    String cancelReason;

    @Column(nullable = false)
    LocalDateTime completedAt;
}
