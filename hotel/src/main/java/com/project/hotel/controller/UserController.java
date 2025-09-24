package com.project.hotel.controller;

import com.project.hotel.dto.request.CreateUserRequest;
import com.project.hotel.dto.response.ApiResponse;
import com.project.hotel.dto.response.UserResponse;
import com.project.hotel.entity.User;
import com.project.hotel.repository.UserRepository;
import com.project.hotel.service.UserService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {
    UserService userService;
    UserRepository userRepository;

    @PostMapping("/register")
    ApiResponse<UserResponse> createUser(@RequestBody @Valid CreateUserRequest request){
        UserResponse userResponse = userService.createUser(request);
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

}
