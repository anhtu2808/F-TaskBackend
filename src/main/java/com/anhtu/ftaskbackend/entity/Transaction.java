package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import com.anhtu.ftaskbackend.enums.TransactionStatus;
import com.anhtu.ftaskbackend.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE transaction SET deleted = 1, update_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Transaction extends AbstractAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "booking_partner_id", referencedColumnName = "id")
    BookingPartner bookingPartner;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    User user;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    @Column(nullable = false)
    Double amount;

    @Column(name = "balance_before")
    Double balanceBefore;
    @Column(name = "balance_after")
    Double balanceAfter;
    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    TransactionStatus status;

}
