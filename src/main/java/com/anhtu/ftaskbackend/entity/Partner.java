package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import java.util.ArrayList;
import java.util.List;

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

    @ElementCollection
    @CollectionTable(name = "partner_districts", joinColumns = @JoinColumn(name = "partner_id"))
    @Column(name = "district_name")
    @Builder.Default
    List<String> districts = new ArrayList<>();

}
