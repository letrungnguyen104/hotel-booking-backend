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
    ROLE_NOT_FOUND(1009, "Role not found!", HttpStatus.NOT_FOUND),
    HOTEL_NOT_FOUND(1010, "Hotel not found!", HttpStatus.NOT_FOUND),
    USERNAME_EXISTED(1011, "Username already in use!", HttpStatus.BAD_REQUEST),
    EMAIL_EXISTED(1012, "Email already in use!", HttpStatus.BAD_REQUEST),
    HOTEL_ADMIN_NOT_FOUND(1013, "Hotel Admin not found!", HttpStatus.NOT_FOUND),
    AMENITY_NOT_FOUND(1014, "Amenity not found!", HttpStatus.NOT_FOUND),
    ROOM_TYPE_NOT_FOUND(1014, "Room type not found!", HttpStatus.NOT_FOUND),
    INVALID_STATUS(1015, "Invalid room type status", HttpStatus.NOT_FOUND),
    ROOM_NUMBER_ALREADY_EXISTS(1016, "Room number already exists!", HttpStatus.BAD_REQUEST),
    ROOM_NOT_FOUND(1017, "Room not found!", HttpStatus.NOT_FOUND),
    SERVICE_NOT_FOUND(1018, "Service not found!", HttpStatus.NOT_FOUND),
    CAN_NOT_DELETE_SELF(1019, "Can not delete self!", HttpStatus.BAD_REQUEST),
    HOTEL_NOT_PENDING(1020, "Hotel not pending!", HttpStatus.BAD_REQUEST),
    HOTEL_ALREADY_BANNED(1021, "Hotel already banned!", HttpStatus.BAD_REQUEST),
    HOTEL_NOT_BANNED(1022, "Hotel note banned!", HttpStatus.BAD_REQUEST),
    NOTIFICATION_NOT_FOUND(1023, "Notification not found!", HttpStatus.NOT_FOUND),
    FORBIDDEN_STATUS_CHANGE(1024, "Forbidden status change!", HttpStatus.BAD_REQUEST),
    INVALID_REQUEST(1025, "Invalid request!", HttpStatus.BAD_REQUEST),
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
