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
    OtpIsInvalid(HttpStatus.BAD_REQUEST.value(), "The provided OTP is invalid", HttpStatus.BAD_REQUEST),
    BookingStartAtInvalid(HttpStatus.BAD_REQUEST.value(), "Start at invalid", HttpStatus.BAD_REQUEST),
    NumberOfPartnerMustBeGreaterOne(HttpStatus.BAD_REQUEST.value(), "Number of partners in required multiple partners service variant must be greater than 1", HttpStatus.BAD_REQUEST),
    NumberOfPartnerMustBeOne(HttpStatus.BAD_REQUEST.value(), "Number of partners in non-required multiple partners service variant must be 1", HttpStatus.BAD_REQUEST),
    InvalidVariantPrice(HttpStatus.BAD_REQUEST.value(), "Variant price must be greater than 0", HttpStatus.BAD_REQUEST),
    InvalidDurationHours(HttpStatus.BAD_REQUEST.value(), "Duration hours must be greater than 0", HttpStatus.BAD_REQUEST),
    BookingAlreadyClaimedByThisPartner(HttpStatus.BAD_REQUEST.value(), "This booking has already been claimed by this partner", HttpStatus.BAD_REQUEST),
    BookingPartnerLimitReached(HttpStatus.BAD_REQUEST.value(), "The booking has reached the partner claim limit", HttpStatus.BAD_REQUEST),
    BookingClaimNotFound(HttpStatus.BAD_REQUEST.value(), "Partner not claim this booking", HttpStatus.BAD_REQUEST),
    InvalidBookingStastusForStart(HttpStatus.BAD_REQUEST.value(), "Booking is not in a valid status to be started", HttpStatus.BAD_REQUEST),
    BookingStartTimeMissing(HttpStatus.BAD_REQUEST.value(), "Booking start time is missing", HttpStatus.BAD_REQUEST),
    BookingStartTimeTooEarly(HttpStatus.BAD_REQUEST.value(), "Too early to start (must be within 15 minutes)", HttpStatus.BAD_REQUEST),
    BookingStatusInvalidForStart(HttpStatus.BAD_REQUEST.value(), "Booking status is not allowed to start", HttpStatus.BAD_REQUEST),
    CustomerNotAcceptedForPartial(HttpStatus.BAD_REQUEST.value(), "Customer has not accepted for PARTIALLY_ACCEPTED booking", HttpStatus.BAD_REQUEST),
    PartnerNotInWorkingStatus(HttpStatus.BAD_REQUEST.value(), "Partner is not in WORKING status", HttpStatus.BAD_REQUEST),
    UserNotMatch(HttpStatus.BAD_REQUEST.value(), "The OTP user is not matched with the phone user", HttpStatus.BAD_REQUEST),
    InvalidPhoneNumber(HttpStatus.BAD_REQUEST.value(), "The provided Phone number is invalid", HttpStatus.BAD_REQUEST),
    BookingNotCompleted(HttpStatus.BAD_REQUEST.value(), "Booking must be completed to create review", HttpStatus.BAD_REQUEST),
    PartnerNotInBooking(HttpStatus.BAD_REQUEST.value(), "Partner did not work on this booking", HttpStatus.BAD_REQUEST),
    ReviewAlreadyExists(HttpStatus.BAD_REQUEST.value(), "You have already reviewed this partner for this booking", HttpStatus.BAD_REQUEST),
    InvalidOrderInfo(HttpStatus.BAD_REQUEST.value(), "The provided order info is invalid", HttpStatus.BAD_REQUEST),
    UnknownType(HttpStatus.BAD_REQUEST.value(), "Unknown payment type", HttpStatus.BAD_REQUEST),
    NotEnoughMoney(HttpStatus.BAD_REQUEST.value(), "Not enough money", HttpStatus.BAD_REQUEST),
    WalletNegativeBalance(HttpStatus.BAD_REQUEST.value(), "Tài khoản của bạn hiện bị vô hiệu hoá một số tính năng vui lòng thanh toán tiền phạt để tiếp tục sử dụng", HttpStatus.BAD_REQUEST),

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
    MissingUserByOtp(HttpStatus.NOT_FOUND.value(), "This OTP is missing its user", HttpStatus.NOT_FOUND),
    UserNotFound(HttpStatus.NOT_FOUND.value(), "User not found", HttpStatus.NOT_FOUND),
    PartnerNotFound(HttpStatus.NOT_FOUND.value(), "Partner not found", HttpStatus.NOT_FOUND),
    ReviewNotFound(HttpStatus.NOT_FOUND.value(), "Review not found", HttpStatus.NOT_FOUND),
    NotificationNotFound(HttpStatus.NOT_FOUND.value(), "Notification not found", HttpStatus.NOT_FOUND),
    FcmNotFound(HttpStatus.NOT_FOUND.value(), "FCM token is invalid or not found", HttpStatus.NOT_FOUND),
    BookingPartnerNotFound(HttpStatus.NO_CONTENT.value(), "Booking partner not found", HttpStatus.NOT_FOUND),
    PaymentNotFoundByBookingId(HttpStatus.NOT_FOUND.value(), "Payment not found with this booking id", HttpStatus.NOT_FOUND),
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
