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
@Table(name = "district")
@SQLDelete(sql = "UPDATE district SET deleted = 1, update_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class District extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false, length = 100, unique = true)
    String name;

    @Column(length = 50)
    String city;

    @Column(length = 20)
    String code;

}

