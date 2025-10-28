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
@SQLDelete(sql = "UPDATE partner SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Partner extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Builder.Default
    Double averageRating = 0.0;

    @Builder.Default
    Integer totalJobsCompleted = 0;

    @Builder.Default
    Boolean isAvailable = true;

    @Column(columnDefinition = "JSON")
    String districtIdsJson;

    @OneToOne
    @JoinColumn(name = "wallet_id", nullable = false)
    Wallet wallet;

}
