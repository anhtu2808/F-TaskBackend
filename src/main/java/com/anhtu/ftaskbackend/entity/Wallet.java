package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
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
@SQLDelete(sql = "UPDATE wallet SET deleted = 1, update_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Wallet extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "partner_id", referencedColumnName = "id")
    Partner partner;

    @Builder.Default
    Double balance = 0.0;
    @Builder.Default
    @Column(name = "total_earned")
    Double totalEarned = 0.0;
    @Builder.Default
    @Column(name = "total_withdrawn")
    Double totalWithdrawn = 0.0;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    User user;


}
