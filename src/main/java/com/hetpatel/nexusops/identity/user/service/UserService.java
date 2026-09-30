package com.hetpatel.nexusops.identity.user.service;

import com.hetpatel.nexusops.common.api.PageResponse;

import com.hetpatel.nexusops.identity.user.dto.request.CreateUserRequest;
import com.hetpatel.nexusops.identity.user.dto.request.UpdateUserRequest;

import com.hetpatel.nexusops.identity.user.dto.response.UserResponse;

import org.springframework.data.domain.Pageable;

public interface UserService {

    UserResponse createUser(CreateUserRequest request);

    UserResponse getUserById(Long id);

    PageResponse<UserResponse> getAllUsers(Pageable pageable);

    UserResponse updateUser(Long id, UpdateUserRequest request);

    void deleteUser(Long id);

    UserResponse restoreUser(Long id);
}
