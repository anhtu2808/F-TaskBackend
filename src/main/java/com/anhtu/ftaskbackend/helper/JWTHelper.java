package com.anhtu.ftaskbackend.helper;

import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

public class JWTHelper {

    /**
     * Get the current user's ID (Long) from JWT in the SecurityContext
     */
    public static Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Long userId = jwt.getClaim("userId");
            if (userId != null) return userId;
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    /**
     * Get current customer's ID from JWT
     */
    public static Long getCurrentCustomerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Long customerId = jwt.getClaim("customerId");
            if (customerId != null) return customerId;
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    /**
     * Get current partner's ID from JWT
     */
    public static Long getCurrentPartnerId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof Jwt jwt) {
            Long partnerId = jwt.getClaim("partnerId");
            if (partnerId != null) return partnerId;
        }
        throw new AppException(ErrorCode.UNAUTHENTICATED);
    }

    /**
     * Check if the current user has a specific authority
     */
    public static boolean hasAuthority(String authority) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null &&
                authentication.getAuthorities().stream()
                              .anyMatch(a -> a.getAuthority().equals(authority));
    }
}
