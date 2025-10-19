package com.project.hotel.mapper;

import com.project.hotel.dto.request.AdminCreateUserRequest;
import com.project.hotel.dto.request.CreateUserRequest;
import com.project.hotel.dto.response.RoleResponse;
import com.project.hotel.dto.response.UserResponse;
import com.project.hotel.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    @Mapping(target = "roles", ignore = true)
    User toUser(CreateUserRequest createUserRequest);

    @Mapping(target = "roles", ignore = true)
    User toUser(AdminCreateUserRequest request);

    @Mapping(target = "roles", source = "roles")
    UserResponse toUserResponse(User user);
    List<UserResponse> toListUserResponse(List<User> listUser);
}
