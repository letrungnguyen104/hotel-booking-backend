// src/main/java/com/project/hotel/service/ChatService.java
package com.project.hotel.service;
import com.project.hotel.dto.request.ChatMessageRequest;
import com.project.hotel.dto.response.ChatMessageResponse;
import com.project.hotel.dto.response.ConversationResponse;
import com.project.hotel.entity.*;
import com.project.hotel.enums.MessageStatus;
import com.project.hotel.enums.MessageType;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.ChatMessageMapper;
import com.project.hotel.repository.*;
import org.springframework.transaction.annotation.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ChatService {
    UserRepository userRepository;
    HotelRepository hotelRepository;
    ChatMessageRepository chatMessageRepository;
    ChatMessageMapper chatMessageMapper;
    RoomTypeRepository roomTypeRepository;
    BookingRoomRepository bookingRoomRepository;
    BookingServiceRepository bookingServiceRepository;


    @Transactional
    public ChatMessageResponse processAndSaveMessage(ChatMessageRequest request, String senderUsername) {
        if (request.getMessageType() == null) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        MessageType messageType;
        try {
            messageType = MessageType.valueOf(request.getMessageType().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new AppException(ErrorCode.INVALID_REQUEST);
        }

        User sender = userRepository.findByUsername(senderUsername)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        User receiver = userRepository.findById(request.getReceiverId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        Hotel hotel = null;
        if (request.getHotelId() != null) {
            hotel = hotelRepository.findById(request.getHotelId())
                    .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));
        }

        ChatMessage chatMessage = ChatMessage.builder()
                .sender(sender)
                .receiver(receiver)
                .hotel(hotel)
                .message(request.getMessage())
                .messageType(messageType)
                .status(MessageStatus.SENT)
                .sentAt(LocalDateTime.now())
                .build();

        ChatMessage savedMessage = chatMessageRepository.save(chatMessage);
        return chatMessageMapper.toChatMessageResponse(savedMessage);
    }

    @Transactional
    public List<ChatMessageResponse> getChatHistory(int recipientId, Integer hotelId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<ChatMessage> messages;

        if(hotelId != null) {
            messages = chatMessageRepository.findChatHistoryWithHotel(currentUser.getId(), recipientId, hotelId);
        } else {
            messages = chatMessageRepository.findChatHistoryWithoutHotel(currentUser.getId(), recipientId);
        }

        List<ChatMessage> unreadMessages = messages.stream()
                .filter(m -> m.getReceiver().getId() == currentUser.getId() && m.getStatus() == MessageStatus.SENT)
                .collect(Collectors.toList());

        if (!unreadMessages.isEmpty()) {
            log.info("Marking {} messages as READ for user {}", unreadMessages.size(), username);
            for (ChatMessage msg : unreadMessages) {
                msg.setStatus(MessageStatus.READ);
            }
            chatMessageRepository.saveAll(unreadMessages);
        }

        return messages.stream()
                .map(chatMessageMapper::toChatMessageResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversations() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));
        List<ChatMessage> lastMessages = chatMessageRepository.findLastMessageOfEachConversation(currentUser.getId());

        return lastMessages.stream().map(msg -> {
            boolean isSender = msg.getSender().getId() == currentUser.getId();
            User partner = isSender ? msg.getReceiver() : msg.getSender();
            long unreadCount = chatMessageRepository.countBySender_IdAndReceiver_IdAndStatus(
                    partner.getId(),
                    currentUser.getId(),
                    MessageStatus.SENT
            );
            Integer hotelId = null;
            String hotelName = null;
            if (msg.getHotel() != null) {
                hotelId = msg.getHotel().getId();
                hotelName = msg.getHotel().getName();
            }

            return ConversationResponse.builder()
                    .conversationPartnerId(partner.getId())
                    .conversationPartnerName(partner.getFullName() != null ? partner.getFullName() : partner.getUsername())
                    .conversationPartnerUsername(partner.getUsername())
                    .conversationPartnerAvatar(partner.getImagePath())
                    .lastMessage(msg.getMessage())
                    .lastMessageSender(isSender ? "me" : "other")
                    .timestamp(msg.getSentAt())
                    .unreadCount(unreadCount)
                    .lastMessageStatus(msg.getStatus().toString())
                    .hotelId(hotelId)
                    .hotelName(hotelName)
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> getConversationsForAdmin(String currentAdminUsername) {
        List<User> users = userRepository.findByUsernameNot(currentAdminUsername);

        return users.stream().map(user -> {
            // TODO: Bổ sung logic lấy tin nhắn cuối cùng và unreadCount
            return ConversationResponse.builder()
                    .conversationPartnerId(user.getId())
                    .conversationPartnerName(user.getFullName() != null ? user.getFullName() : user.getUsername())
                    .conversationPartnerUsername(user.getUsername())
                    .conversationPartnerAvatar(user.getImagePath())
                    .lastMessage("...")
                    .timestamp(LocalDateTime.now())
                    .unreadCount(0)
                    .hotelId(null)
                    .build();
        }).collect(Collectors.toList());
    }

    // (Hàm này có thể bị thiếu từ file BookingService, tôi thêm lại vào đây)
    private String loadRoomTypeName(Room room) {
        RoomType rt = roomTypeRepository.findById(room.getRoomType().getId())
                .orElseThrow(() -> new AppException(ErrorCode.ROOM_TYPE_NOT_FOUND));
        return rt.getName();
    }
}