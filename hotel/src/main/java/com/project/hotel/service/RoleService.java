package com.project.hotel.service;

import com.project.hotel.dto.request.RoleRequest;
import com.project.hotel.dto.response.RoleResponse;
import com.project.hotel.entity.Role;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.RoleMapper;
import com.project.hotel.repository.RoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleService {
    RoleRepository roleRepository;
    RoleMapper roleMapper;

    public RoleResponse createRole(RoleRequest request) {
        if (roleRepository.existsById(request.getRoleName())) {
            throw new AppException(ErrorCode.USER_EXISTED); // bạn có thể thêm ErrorCode mới: ROLE_EXISTED
        }
        Role role = roleMapper.toRole(request);
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream().map(roleMapper::toRoleResponse)
                .toList();
    }

    public RoleResponse getRole(String roleName) {
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return roleMapper.toRoleResponse(role);
    }

    public RoleResponse updateRole(String roleName, RoleRequest request) {
        Role role = roleRepository.findById(roleName)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        role.setDescription(request.getDescription());
        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    public void deleteRole(String roleName) {
        if (!roleRepository.existsById(roleName)) {
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        }
        roleRepository.deleteById(roleName);
    }
}