package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import com.anhtu.ftaskbackend.enums.Gender;
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
@SQLDelete(sql = "UPDATE user SET deleted = 1, updated_at = CURRENT_TIMESTAMP WHERE id = ?")
@SQLRestriction("deleted = 0")
public class User extends AbstractAuditingEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(length = 100, unique = true)
    String username;

    @Column(length = 12, unique = true)
    String phone;

    @Column(length = 100, unique = true)
    String email;

    @Column(length = 100)
    String password;

    @Builder.Default
    Boolean isActive = true;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    Role role;

    @Enumerated(EnumType.STRING)
    Gender gender;

    @Column(length = 12, unique = true)
    String idCard;
    String fullName;
    String address;
    @Column(name = "avatar_url", columnDefinition = "TEXT")
    String avatarUrl;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    Customer customer;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    Partner partner;

    @OneToOne
    @JoinColumn(name = "wallet_id", referencedColumnName = "id", nullable = false)
    Wallet wallet;
}

