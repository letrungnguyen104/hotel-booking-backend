package com.project.hotel.service;

import com.project.hotel.dto.request.CreateSpecialPriceRequest;
import com.project.hotel.dto.response.SpecialPriceResponse;
import com.project.hotel.entity.RoomType;
import com.project.hotel.entity.SpecialPrice;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.SpecialPriceMapper;
import com.project.hotel.repository.RoomTypeRepository;
import com.project.hotel.repository.SpecialPriceRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SpecialPriceService {
    SpecialPriceRepository specialPriceRepository;
    RoomTypeRepository roomTypeRepository;
    SpecialPriceMapper specialPriceMapper;

    public SpecialPriceResponse createSpecialPrice(CreateSpecialPriceRequest request) {
        RoomType roomType = roomTypeRepository.findById(request.getRoomTypeId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));

        SpecialPrice specialPrice = SpecialPrice.builder()
                .roomType(roomType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .price(request.getPrice())
                .build();

        return specialPriceMapper.toResponse(specialPriceRepository.save(specialPrice));
    }

    public List<SpecialPriceResponse> getSpecialPricesForRoomType(Integer roomTypeId) {
        List<SpecialPrice> prices = specialPriceRepository.findByRoomTypeId(roomTypeId);
        return specialPriceMapper.toResponseList(prices);
    }

    public void deleteSpecialPrice(Integer specialPriceId) {
        specialPriceRepository.deleteById(specialPriceId);
    }
}