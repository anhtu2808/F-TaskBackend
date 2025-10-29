package com.anhtu.ftaskbackend.entity;

import com.anhtu.ftaskbackend.common.AbstractAuditingEntity;
import com.anhtu.ftaskbackend.enums.OtpType;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
public class Otp extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    OtpType otpType;

    @Column(length = 6, nullable = false)
    String otpCode;

    @Builder.Default
    Boolean isUsed = false;

    @Column(name = "expired_at", nullable = false)
    LocalDateTime expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    User user;

}
