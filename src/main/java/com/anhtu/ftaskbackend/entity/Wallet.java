package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@SQLDelete(sql = "UPDATE wallet SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Wallet extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Builder.Default
    Double balance = 0.0;
    @Builder.Default
    Double totalEarned = 0.0;
    @Builder.Default
    Double totalWithdrawn = 0.0;

}
