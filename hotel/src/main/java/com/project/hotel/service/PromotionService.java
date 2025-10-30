package com.project.hotel.service;

import com.project.hotel.dto.request.PromotionRequest;
import com.project.hotel.dto.request.ValidatePromotionRequest;
import com.project.hotel.dto.response.PromotionResponse;
import com.project.hotel.dto.response.ValidatePromotionResponse;
import com.project.hotel.entity.Promotion;
import com.project.hotel.enums.PromotionStatus;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.PromotionMapper;
import com.project.hotel.repository.PromotionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class PromotionService {

    private final PromotionRepository promotionRepository;
    private final PromotionMapper promotionMapper;
    private final FileStorageService fileStorageService;

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PromotionResponse createPromotion(PromotionRequest request, MultipartFile file) {
        if (request.getCode() != null && !request.getCode().isEmpty() && promotionRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.PROMOTION_CODE_EXISTED);
        }
        Promotion promotion = promotionMapper.toPromotion(request);

        if (request.getIsFeatured() != null) {
            promotion.setFeatured(request.getIsFeatured());
        }

        if (file != null && !file.isEmpty()) {
            String imageUrl = fileStorageService.saveFile(file);
            promotion.setImageUrl(imageUrl);
        }

        promotion = promotionRepository.save(promotion);
        return promotionMapper.toPromotionResponse(promotion);
    }

    @Transactional
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public PromotionResponse updatePromotion(Integer id, PromotionRequest request, MultipartFile file) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        String oldImageUrl = promotion.getImageUrl();
        if (request.getCode() != null && !request.getCode().isEmpty() && !request.getCode().equals(promotion.getCode())
                && promotionRepository.existsByCode(request.getCode())) {
            throw new AppException(ErrorCode.PROMOTION_CODE_EXISTED);
        }
        promotionMapper.updatePromotionFromDto(request, promotion);

        if (request.getIsFeatured() != null) {
            promotion.setFeatured(request.getIsFeatured());
        }

        if (file != null && !file.isEmpty()) {
            if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
                fileStorageService.deleteFile(oldImageUrl);
            }
            String newImageUrl = fileStorageService.saveFile(file);
            promotion.setImageUrl(newImageUrl);
        } else if (request.getImageUrl() == null && oldImageUrl != null) {
            fileStorageService.deleteFile(oldImageUrl);
            promotion.setImageUrl(null);
        }

        promotion = promotionRepository.save(promotion);
        return promotionMapper.toPromotionResponse(promotion);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public void deletePromotion(Integer id) {
        Promotion promotion = promotionRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));
        promotion.setStatus(PromotionStatus.INACTIVE);
        promotionRepository.save(promotion);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public List<PromotionResponse> getAllPromotionsForAdmin() {
        return promotionMapper.toPromotionResponseList(promotionRepository.findAll());
    }

    public List<PromotionResponse> getActivePromotions() {
        return promotionMapper.toPromotionResponseList(
                promotionRepository.findByStatus(PromotionStatus.ACTIVE)
        );
    }

    public List<PromotionResponse> getFeaturedPromotions() {
        return promotionMapper.toPromotionResponseList(
                promotionRepository.findByIsFeaturedTrueAndStatus(PromotionStatus.ACTIVE)
        );
    }

    public ValidatePromotionResponse validatePromotion(ValidatePromotionRequest request) {
        log.info("--- BẮT ĐẦU KIỂM TRA MÃ GIẢM GIÁ ---");
        log.info("Mã nhận được: {}", request.getCode());
        log.info("Giá gốc: {}", request.getBasePrice());
        Promotion promotion = promotionRepository.findByCodeAndStatus(request.getCode(), PromotionStatus.ACTIVE)
                .orElse(null);
        if (promotion == null) {
            return ValidatePromotionResponse.builder().valid(false).message("Invalid promotion code").build();
        }
        LocalDate today = LocalDate.now();
        if (today.isBefore(promotion.getStartDate()) || today.isAfter(promotion.getEndDate())) {
            return ValidatePromotionResponse.builder().valid(false).message("This code is expired or not yet active.").build();
        }
        if (request.getBasePrice() < promotion.getMinSpend()) {
            return ValidatePromotionResponse.builder().valid(false)
                    .message("Minimum spend of " + promotion.getMinSpend() + " is required.")
                    .build();
        }
        double discountAmount = request.getBasePrice() * (promotion.getDiscountPercent() / 100);

        if (promotion.getMaxDiscountAmount() != null && discountAmount > promotion.getMaxDiscountAmount()) {
            discountAmount = promotion.getMaxDiscountAmount();
        }
        double finalPrice = request.getBasePrice() - discountAmount;
        return ValidatePromotionResponse.builder()
                .valid(true)
                .message("Code applied successfully!")
                .discountAmount(discountAmount)
                .finalPrice(finalPrice)
                .build();
    }
}