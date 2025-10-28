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
@SQLDelete(sql = "UPDATE transaction SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Transaction extends AbstractAuditingEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @ManyToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id")
    Wallet wallet;

    @ManyToOne
    @JoinColumn(name = "booking_partner_id", referencedColumnName = "id")
    BookingPartner bookingPartner;

    @Enumerated(EnumType.STRING)
    TransactionType type;

    @Column(nullable = false)
    Double amount;

    Double balanceBefore;

    Double balanceAfter;
    @Column(columnDefinition = "TEXT")
    String description;

    @Enumerated(EnumType.STRING)
    TransactionStatus status;

}
