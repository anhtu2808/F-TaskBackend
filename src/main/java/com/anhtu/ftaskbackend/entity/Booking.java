package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import com.anhtu.ftaskbackend.enums.BookingStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

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
    @JoinColumn(name = "customer_id", referencedColumnName = "id", nullable = false)
    Customer customer;

    @ManyToOne
    @JoinColumn(name = "variant_id", referencedColumnName = "id", nullable = false)
    ServiceCatalogVariant variant;

    @ManyToOne
    @JoinColumn(name = "address_id", referencedColumnName = "id", nullable = false)
    Address address;

    @Column(nullable = false, name = "start_at")
    LocalDateTime startAt;

    @Column(nullable = false, name = "total_price")
    Double totalPrice;

    @Column(nullable = false, name = "platform_fee")
    Double platformFee;

    @Column(columnDefinition = "TEXT", name = "customer_note")
    String customerNote;

    @Column(name = "required_partners") @Builder.Default
    Integer requiredPartners = 1;

    @Builder.Default
    BookingStatus status = BookingStatus.PENDING;

    @Column(columnDefinition = "TEXT", name = "cancel_reason")
    String cancelReason;

    @Column(nullable = false)
    LocalDateTime completedAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.REMOVE, fetch = FetchType.EAGER)
    Set<BookingPartner> partners = new HashSet<>();
}
