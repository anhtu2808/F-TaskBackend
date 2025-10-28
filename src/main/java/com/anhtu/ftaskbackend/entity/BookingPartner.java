package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE booking_partner SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class BookingPartner extends AbstractAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id", referencedColumnName = "id")
    Booking booking;

    @ManyToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    Partner partner;

    @Column(nullable = false)
    Double partnerEarnings;

    @Enumerated(EnumType.STRING)
    BookingPartnerStatus status;

    @Column(columnDefinition = "TEXT")
    String cancelReason;

    @Column(name = "join_at", updatable = false)
    @Builder.Default
    LocalDate joinAt = LocalDate.now();

}
