package com.project.hotel.mapper;

import com.project.hotel.dto.request.CreateUserRequest;
import com.project.hotel.dto.response.RoleResponse;
import com.project.hotel.dto.response.UserResponse;
import com.project.hotel.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {RoleMapper.class})
public interface UserMapper {
    User toUser(CreateUserRequest createUserRequest);

    @Mapping(target = "roles", source = "role")
    UserResponse toUserResponse(User user);
    List<UserResponse> toListUserResponse(List<User> listUser);
}
