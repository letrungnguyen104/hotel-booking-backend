package com.project.hotel.service;

import com.project.hotel.dto.request.CreateHotelAdminRequest;
import com.project.hotel.dto.request.UpdateHotelAdminRequest;
import com.project.hotel.dto.response.HotelAdminResponse;
import com.project.hotel.dto.response.UserResponse;
import com.project.hotel.entity.HotelAdminProfile;
import com.project.hotel.entity.Role;
import com.project.hotel.entity.User;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.HotelAdminProfileMapper;
import com.project.hotel.mapper.UserMapper;
import com.project.hotel.repository.HotelAdminProfileRepository;
import com.project.hotel.repository.RoleRepository;
import com.project.hotel.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class HotelAdminProfileService {

    HotelAdminProfileRepository hotelAdminProfileRepository;
    HotelAdminProfileMapper hotelAdminProfileMapper;
    UserRepository userRepository;
    UserMapper userMapper;
    RoleRepository roleRepository;

    public HotelAdminResponse createHotelAdmin(CreateHotelAdminRequest request) {
        HotelAdminProfile hotelAdminProfile = hotelAdminProfileMapper.toHotelAdminProfile(request);
        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        hotelAdminProfile.setUser(owner);
        hotelAdminProfile.setVerified(0);
        return hotelAdminProfileMapper
                .toHotelAdminResponse(hotelAdminProfileRepository.save(hotelAdminProfile));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public HotelAdminResponse verifyHotelAdmin(int profileId) {
        HotelAdminProfile profile = hotelAdminProfileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ADMIN_NOT_FOUND));
        profile.setVerified(1);
        User owner = profile.getUser();
        Role hotelAdminRole = roleRepository.findById("HOTEL_ADMIN")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        owner.getRoles().add(hotelAdminRole);
        userRepository.save(owner);
        hotelAdminProfileRepository.save(profile);
        return hotelAdminProfileMapper.toHotelAdminResponse(profile);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMIN', 'ROLE_HOTEL_ADMIN')")
    public HotelAdminResponse updateHotelAdmin(int profileId, UpdateHotelAdminRequest request) {
        HotelAdminProfile profile = hotelAdminProfileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ADMIN_NOT_FOUND));
        profile.setBusinessName(request.getBusinessName());
        profile.setBusinessAddress(request.getBusinessAddress());
        profile.setTaxCode(request.getTaxCode());
        profile.setLicenseNumber(request.getLicenseNumber());
        profile.setBankAccount(request.getBankAccount());
        profile.setIdCardOrPassport(request.getIdCardOrPassport());
        return hotelAdminProfileMapper.toHotelAdminResponse(hotelAdminProfileRepository.save(profile));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deleteHotelAdmin(int profileId) {
        HotelAdminProfile profile = hotelAdminProfileRepository.findById(profileId)
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ADMIN_NOT_FOUND));
        User owner = profile.getUser();
        Role hotelAdminRole = roleRepository.findById("HOTEL_ADMIN")
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        owner.getRoles().remove(hotelAdminRole);
        userRepository.save(owner);
        hotelAdminProfileRepository.delete(profile);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<HotelAdminResponse> getListHotelAdmin() {
        return hotelAdminProfileRepository.findAll().stream()
                .map(hotelAdminProfileMapper::toHotelAdminResponse).toList();
    }

    public HotelAdminResponse getMyBusinessProfile() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        HotelAdminProfile profile = hotelAdminProfileRepository.findByUser_Id(user.getId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_ADMIN_NOT_FOUND));

        return hotelAdminProfileMapper.toHotelAdminResponse(profile);
    }

    public Optional<HotelAdminResponse> checkMyBusinessProfile() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        return hotelAdminProfileRepository.findByUser_Id(user.getId())
                .map(hotelAdminProfileMapper::toHotelAdminResponse);
    }

}
