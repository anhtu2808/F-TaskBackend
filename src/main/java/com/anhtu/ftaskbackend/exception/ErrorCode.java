package com.anhtu.ftaskbackend.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    // 500
    UNCATEGORIZED_EXCEPTION(HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR),

    //401
    UNAUTHENTICATED(HttpStatus.UNAUTHORIZED.value(), "Unauthenticated", HttpStatus.UNAUTHORIZED),
    // 403 Forbidden
    UNAUTHORIZED(HttpStatus.FORBIDDEN.value(), "You are not authorized to access", HttpStatus.FORBIDDEN),

    //400 Bad Request
    BadRequest(HttpStatus.BAD_REQUEST.value(), "Bad Request", HttpStatus.BAD_REQUEST),
    DuplicatedEmail(HttpStatus.BAD_REQUEST.value(), "This email has been used before, try another please!", HttpStatus.BAD_REQUEST),
    DuplicatedPhone(HttpStatus.BAD_REQUEST.value(), "This phone number has been used before, try another please!", HttpStatus.BAD_REQUEST),
    DuplicatedUsername(HttpStatus.BAD_REQUEST.value(), "This username has been used before, try another please!", HttpStatus.BAD_REQUEST),
    WrongPassword(HttpStatus.BAD_REQUEST.value(), "Provided password is wrong", HttpStatus.BAD_REQUEST),
    OtpIsExpired(HttpStatus.BAD_REQUEST.value(), "The OTP is expired", HttpStatus.BAD_REQUEST),
    OtpIsNotSuitable(HttpStatus.BAD_REQUEST.value(), "The OTP isn't suitable", HttpStatus.BAD_REQUEST),
    BookingStartAtInvalid(HttpStatus.BAD_REQUEST.value(), "Start at invalid", HttpStatus.BAD_REQUEST),

    //404 Not found
    UserNotFoundByPhone(HttpStatus.NOT_FOUND.value(), "This user does not exist with this phone number", HttpStatus.NOT_FOUND),
    OtpNotFoundByCode(HttpStatus.NOT_FOUND.value(), "Otp does not exist with this code", HttpStatus.NOT_FOUND),
    RoleNotFoundByName(HttpStatus.NOT_FOUND.value(), "This role does not exist with this name", HttpStatus.NOT_FOUND),
    SERVICE_CATALOG_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "Service Catalog not found", HttpStatus.NOT_FOUND),
    ServiceVariantNotFound(HttpStatus.NOT_FOUND.value(), "Service Variant not found", HttpStatus.NOT_FOUND),
    AddressNotFound(HttpStatus.NOT_FOUND.value(), "Address not found", HttpStatus.NOT_FOUND),
    CustomerNotFoundByUsername(HttpStatus.NOT_FOUND.value(), "Customer does not exist with this username", HttpStatus.NOT_FOUND),
    BookingNotFound(HttpStatus.NOT_FOUND.value(), "Booking not found", HttpStatus.NOT_FOUND),
    CustomerNotFound(HttpStatus.NOT_FOUND.value(), "Customer not found", HttpStatus.NOT_FOUND),
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private final int code;
    private final String message;
    private final HttpStatusCode statusCode;

}
