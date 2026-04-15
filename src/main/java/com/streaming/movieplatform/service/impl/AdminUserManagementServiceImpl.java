package com.streaming.movieplatform.service.impl;

import com.streaming.movieplatform.dto.AdminUserUpdateRequest;
import com.streaming.movieplatform.entity.Role;
import com.streaming.movieplatform.entity.User;
import com.streaming.movieplatform.enums.RoleName;
import com.streaming.movieplatform.exception.ResourceNotFoundException;
import com.streaming.movieplatform.repository.RoleRepository;
import com.streaming.movieplatform.repository.UserRepository;
import com.streaming.movieplatform.service.AdminUserManagementService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional
public class AdminUserManagementServiceImpl implements AdminUserManagementService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public AdminUserManagementServiceImpl(UserRepository userRepository,
                                          RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay nguoi dung"));
    }

    @Override
    public User updateUser(AdminUserUpdateRequest request) {
        User user = getUserById(request.getId());
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setEnabled(request.isEnabled());

        Set<Role> roles = new HashSet<>();
        List<String> requestedRoles = request.getRoleNames() == null ? List.of() : request.getRoleNames();
        for (String roleName : requestedRoles) {
            roles.add(roleRepository.findByName(RoleName.valueOf(roleName))
                    .orElseThrow(() -> new ResourceNotFoundException("Khong tim thay role " + roleName)));
        }
        user.setRoles(roles);
        return userRepository.save(user);
    }
}
