package com.streaming.movieplatform.service;

import com.streaming.movieplatform.dto.AdminUserUpdateRequest;
import com.streaming.movieplatform.entity.Role;
import com.streaming.movieplatform.entity.User;

import java.util.List;

public interface AdminUserManagementService {
    List<User> getAllUsers();
    List<Role> getAllRoles();
    User getUserById(Long userId);
    User updateUser(AdminUserUpdateRequest request);
}
