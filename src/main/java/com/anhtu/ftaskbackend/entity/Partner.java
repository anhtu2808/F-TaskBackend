package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@SQLDelete(sql = "UPDATE partner SET deleted = 1, update_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class Partner extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

    @Builder.Default
    @Column(name = "average_rating")
    Double averageRating = 0.0;

    @Builder.Default
    @Column(name = "total_jobs_completed")
    Integer totalJobsCompleted = 0;

    @Builder.Default
    @Column(name = "is_available")
    Boolean isAvailable = true;

    @ManyToMany
    @JoinTable(
            name = "partner_district",
            joinColumns = @JoinColumn(name = "partner_id"),
            inverseJoinColumns = @JoinColumn(name = "district_id")
    )
    @Builder.Default
    Set<District> districts = new HashSet<>();

}
