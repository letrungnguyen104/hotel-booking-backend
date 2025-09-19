package com.project.hotel.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

@Getter
public enum ErrorCode {
    UNCATEGORIED_EXCEPTION(9999, "Uncategorized Exception!", HttpStatus.INTERNAL_SERVER_ERROR),
    KEY_INVALID(1001, "Invalid message key!", HttpStatus.BAD_REQUEST),
    USER_EXISTED(1002, "User existed!", HttpStatus.BAD_REQUEST),
    USERNAME_INVALID(1003, "Username must be at least 8 characters!", HttpStatus.BAD_REQUEST),
    PASSWORD_INVALID(1004, "Password must be at least 8 characters!", HttpStatus.BAD_REQUEST),
    USER_NOT_EXISTED(1005, "User not found!", HttpStatus.NOT_FOUND),
    EMAIL_INVALID(1006, "Incorrect email format!", HttpStatus.BAD_REQUEST),
    UNAUTHENTICATED(1007, "Unauthenticated!", HttpStatus.UNAUTHORIZED),
    UNAUTHORIZED(1008, "You do not have permission!", HttpStatus.FORBIDDEN),
    ROLE_NOT_FOUND(1009, "Role not found!", HttpStatus.NOT_FOUND)
    ;

    ErrorCode(int code, String message, HttpStatusCode statusCode) {
        this.code = code;
        this.message = message;
        this.statusCode = statusCode;
    }

    private int code;
    private String message;
    private HttpStatusCode statusCode;
}
