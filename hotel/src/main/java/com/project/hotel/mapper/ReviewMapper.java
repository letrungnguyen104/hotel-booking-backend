package com.project.hotel.mapper;

import com.project.hotel.dto.response.ReviewResponse;
import com.project.hotel.entity.Review;
import com.project.hotel.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(source = "user", target = "user")
    ReviewResponse toReviewResponse(Review review);

    List<ReviewResponse> toReviewResponseList(List<Review> reviews);

    @Mapping(source = "fullName", target = "fullName")
    @Mapping(source = "imagePath", target = "avatarUrl")
    ReviewResponse.UserInfo userToUserInfo(User user);
}