package com.project.hotel.service;

import com.project.hotel.dto.request.CreateReviewRequest;
import com.project.hotel.dto.response.ReviewResponse;
import com.project.hotel.entity.Booking;
import com.project.hotel.entity.Review;
import com.project.hotel.entity.User;
import com.project.hotel.enums.BookingStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.ReviewMapper;
import com.project.hotel.repository.BookingRepository;
import com.project.hotel.repository.ReviewRepository;
import com.project.hotel.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ReviewService {

    ReviewRepository reviewRepository;
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ReviewMapper reviewMapper;
    NotificationService notificationService;

    @Transactional
    @PreAuthorize("isAuthenticated()")
    public ReviewResponse createReview(CreateReviewRequest request) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Booking booking = bookingRepository.findById(request.getBookingId())
                .orElseThrow(() -> new AppException(ErrorCode.BOOKING_NOT_FOUND));

        if (booking.getUser().getId() != user.getId()) {
            throw new AppException(ErrorCode.UNAUTHORIZED);
        }
        if (booking.getStatus() != BookingStatus.COMPLETED) {
            throw new AppException(ErrorCode.BOOKING_NOT_COMPLETED);
        }
        if (reviewRepository.existsByBookingId(request.getBookingId())) {
            throw new AppException(ErrorCode.REVIEW_ALREADY_EXISTS);
        }

        Review review = Review.builder()
                .user(user)
                .hotel(booking.getHotel())
                .booking(booking)
                .rating(request.getRating())
                .comment(request.getComment())
                .createdAt(LocalDateTime.now())
                .build();

        Review savedReview = reviewRepository.save(review);

        // (Tùy chọn) Gửi thông báo cho chủ khách sạn
        // notificationService.notifyNewReview(savedReview);

        return reviewMapper.toReviewResponse(savedReview);
    }

    @Transactional(readOnly = true)
    public List<ReviewResponse> getReviewsForHotel(Integer hotelId) {
        List<Review> reviews = reviewRepository.findByHotelIdOrderByCreatedAtDesc(hotelId);
        return reviewMapper.toReviewResponseList(reviews);
    }
}