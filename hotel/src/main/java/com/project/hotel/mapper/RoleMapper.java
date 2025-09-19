package com.project.hotel.mapper;

import com.project.hotel.dto.request.RoleRequest;
import com.project.hotel.dto.response.RoleResponse;
import com.project.hotel.entity.Role;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleResponse toRoleResponse(Role role);
    Role toRole(RoleRequest request);
}
