package com.project.hotel.service;

import com.project.hotel.dto.request.GuestInquiryRequest;
import com.project.hotel.entity.GuestInquiry;
import com.project.hotel.enums.GuestInquiryStatus;
import com.project.hotel.repository.GuestInquiryRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GuestInquiryService {

    GuestInquiryRepository guestInquiryRepository;
    EmailService emailService;

    @Transactional
    public void createInquiry(GuestInquiryRequest request) {
        GuestInquiry inquiry = GuestInquiry.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .message(request.getMessage())
                .status(GuestInquiryStatus.PENDING)
                .build();

        guestInquiryRepository.save(inquiry);

        String subject = "HotelVerse Support: We've received your request";
        String body = String.format(
                "Hi %s,\n\n" +
                        "Thank you for contacting us. We have received your message and our team will review it shortly.\n\n" +
                        "Your message:\n\"%s\"\n\n" +
                        "Best regards,\n" +
                        "The HotelVerse Team",
                request.getFullName(),
                request.getMessage()
        );
        emailService.sendSimpleMessage(request.getEmail(), subject, body);
    }

    public List<GuestInquiry> getAllInquiries() {
        return guestInquiryRepository.findAll();
    }

    @Transactional
    public GuestInquiry replyToInquiry(Integer inquiryId, Map<String, String> requestBody) {
        String replyMessage = requestBody.get("reply");
        if (replyMessage == null || replyMessage.isEmpty()) {
            throw new IllegalArgumentException("Reply message is required");
        }

        GuestInquiry inquiry = guestInquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new RuntimeException("Inquiry not found"));

        inquiry.setAdminReply(replyMessage);
        inquiry.setStatus(GuestInquiryStatus.RESOLVED);
        GuestInquiry savedInquiry = guestInquiryRepository.save(inquiry);

        String subject = "Re: HotelVerse Support Request";
        String body = String.format(
                "Hi %s,\n\n" +
                        "Our support team has responded to your inquiry:\n\n" +
                        "--- Admin Reply ---\n" +
                        "%s\n" +
                        "--- Your Original Message ---\n" +
                        "\"%s\"\n\n" +
                        "Best regards,\n" +
                        "The HotelVerse Team",
                inquiry.getFullName(),
                replyMessage,
                inquiry.getMessage()
        );
        emailService.sendSimpleMessage(inquiry.getEmail(), subject, body);

        return savedInquiry;
    }
}