package com.project.hotel.service;

import com.project.hotel.dto.request.CreateAmenityRequest;
import com.project.hotel.dto.response.AmenityResponse;
import com.project.hotel.entity.Amenity;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.AmenityMapper;
import com.project.hotel.repository.AmenityRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AmenityService {

    AmenityRepository amenityRepository;
    AmenityMapper amenityMapper;

    public AmenityResponse createAmenity(CreateAmenityRequest request) {
        Optional<Amenity> existingAmenity = amenityRepository.findByName(request.getName());

        if (existingAmenity.isPresent()) {
            return amenityMapper.toAmenityResponse(existingAmenity.get());
        }

        Amenity amenity = amenityMapper.toAmenity(request);
        Amenity saved = amenityRepository.save(amenity);
        return amenityMapper.toAmenityResponse(saved);
    }

    public List<AmenityResponse> getAllAmenities() {
        return amenityRepository.findAll()
                .stream()
                .map(amenityMapper::toAmenityResponse)
                .collect(Collectors.toList());
    }

    public AmenityResponse getAmenityById(int id) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AMENITY_NOT_FOUND));
        return amenityMapper.toAmenityResponse(amenity);
    }

    public AmenityResponse updateAmenity(int id, CreateAmenityRequest request) {
        Amenity amenity = amenityRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.AMENITY_NOT_FOUND));
        amenity.setName(request.getName());
        Amenity updated = amenityRepository.save(amenity);
        return amenityMapper.toAmenityResponse(updated);
    }

    public void deleteAmenity(int id) {
        if (!amenityRepository.existsById(id)) {
            throw new AppException(ErrorCode.AMENITY_NOT_FOUND);
        }
        amenityRepository.deleteById(id);
    }
}