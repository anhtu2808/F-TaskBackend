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
