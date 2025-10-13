package com.project.hotel.controller;

import com.cloudinary.Api;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.hotel.dto.request.ChangePasswordRequest;
import com.project.hotel.dto.request.CreateUserRequest;
import com.project.hotel.dto.request.UpdateProfileRequest;
import com.project.hotel.dto.request.VerifyRegisterRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.UserResponse;
import com.project.hotel.entity.User;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.service.EmailVerificationService;
import com.project.hotel.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    UserRepository userRepository;
    EmailVerificationService verificationService;

    Map<String, CreateUserRequest> tempUsers = new HashMap<>();

    @PostMapping("/pre-register")
    public ApiResponse<String> preRegister(@RequestBody @Valid CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new AppException(ErrorCode.USERNAME_EXISTED);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        verificationService.sendVerificationCode(request.getEmail());
        tempUsers.put(request.getEmail(), request);
        return ApiResponse.<String>builder()
                .message("Verification code sent to " + request.getEmail())
                .build();
    }

    @PostMapping("/verify-register")
    public ApiResponse<UserResponse> verifyRegister(@RequestBody VerifyRegisterRequest request) {
        boolean success = verificationService.verifyCode(request.getEmail(), request.getCode());
        if (!success) {
            throw new AppException(ErrorCode.KEY_INVALID);
        }
        CreateUserRequest preUser = tempUsers.remove(request.getEmail());
        if (preUser == null) {
            throw new AppException(ErrorCode.USER_NOT_EXISTED);
        }
        UserResponse userResponse = userService.createUser(preUser);
        return ApiResponse.<UserResponse>builder()
                .result(userResponse)
                .build();
    }

    @GetMapping("/get-all")
    public ApiResponse<List<UserResponse>> getAllUsers() {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userService.getAllUsers())
                .build();
    }

    @GetMapping("/debug-user/{id}")
    public ResponseEntity<User> getUser(@PathVariable int id) {
        return ResponseEntity.of(userRepository.findById(id));
    }

    @GetMapping("get-user/{id}")
    public ApiResponse<UserResponse> getUserById(@PathVariable("id") int id) {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getUser(id))
                .build();
    }

    @GetMapping("/my-profile")
    public ApiResponse<UserResponse> getProfile() {
        return ApiResponse.<UserResponse>builder()
                .result(userService.getProfile())
                .build();
    }

    @PutMapping(value = "/my-profile", consumes = "multipart/form-data")
    public ApiResponse<UserResponse> updateMyProfile(
            @RequestPart("request") String requestJson,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws JsonProcessingException {

        UpdateProfileRequest request = new ObjectMapper().readValue(requestJson, UpdateProfileRequest.class);

        return ApiResponse.<UserResponse>builder()
                .result(userService.updateMyProfile(request, file))
                .build();
    }

    @PutMapping("/my-profile/change-password")
    public ApiResponse<String> changeMyPassword(@RequestBody ChangePasswordRequest request) {
        userService.changeMyPassword(request);
        return ApiResponse.<String>builder()
                .result("Password changed successfully!")
                .build();
    }
}
