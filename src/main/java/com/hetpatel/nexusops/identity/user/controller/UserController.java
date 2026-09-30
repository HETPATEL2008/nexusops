package com.hetpatel.nexusops.identity.user.controller;

import com.hetpatel.nexusops.common.api.ApiResponse;
import com.hetpatel.nexusops.common.api.ApiStatus;
import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.identity.user.dto.request.CreateUserRequest;
import com.hetpatel.nexusops.identity.user.dto.request.UpdateUserRequest;

import com.hetpatel.nexusops.identity.user.dto.response.UserResponse;

import com.hetpatel.nexusops.identity.user.service.UserService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Pageable;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> createUser(
            @Valid @RequestBody CreateUserRequest request) {

        UserResponse response = userService.createUser(request);

        ApiResponse<UserResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.CREATED,
                        "User created successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable Long id) {

        UserResponse response = userService.getUserById(id);

        ApiResponse<UserResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "User retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<PageResponse<UserResponse>>> getAllUsers(Pageable pageable) {

        PageResponse<UserResponse> response = userService.getAllUsers(pageable);

        ApiResponse<PageResponse<UserResponse>> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "All users retrieved successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> updateUser(
        @PathVariable Long id,
        @Valid @RequestBody UpdateUserRequest request) {

        UserResponse response = userService.updateUser(id, request);

        ApiResponse<UserResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "User updated successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {

        userService.deleteUser(id);

        ApiResponse<Void> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "User deleted successfully.",
                        null
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> restoreUser(@PathVariable Long id) {

        UserResponse response = userService.restoreUser(id);

        ApiResponse<UserResponse> apiResponse =
                new ApiResponse<>(
                        true,
                        ApiStatus.SUCCESS,
                        "User restored successfully.",
                        response
                );

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(apiResponse);
    }
}
