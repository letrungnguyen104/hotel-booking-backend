// src/main/java/com/project/hotel/service/ChatService.java
package com.project.hotel.service;
import com.project.hotel.dto.request.ChatMessageRequest;
import com.project.hotel.dto.response.ChatMessageResponse;
import com.project.hotel.dto.response.ConversationResponse;
import com.project.hotel.entity.ChatMessage;
import com.project.hotel.entity.Hotel;
import com.project.hotel.entity.User;
import com.project.hotel.enums.MessageStatus;
import com.project.hotel.enums.MessageType;
import com.project.hotel.exception.AppException;
import com.project.hotel.exception.ErrorCode;
import com.project.hotel.mapper.ChatMessageMapper;
import com.project.hotel.repository.ChatMessageRepository;
import com.project.hotel.repository.HotelRepository;
import com.project.hotel.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatService {
    UserRepository userRepository;
    HotelRepository hotelRepository;
    ChatMessageRepository chatMessageRepository;
    ChatMessageMapper chatMessageMapper;

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

        if (request.getHotelId() == null) {
            throw new AppException(ErrorCode.HOTEL_NOT_FOUND);
        }
        Hotel hotel = hotelRepository.findById(request.getHotelId())
                .orElseThrow(() -> new AppException(ErrorCode.HOTEL_NOT_FOUND));

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

    public List<ChatMessageResponse> getChatHistory(int recipientId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        List<ChatMessage> messages = chatMessageRepository.findChatHistory(currentUser.getId(), recipientId);
        List<ChatMessage> unreadMessages = messages.stream()
                .filter(m -> m.getReceiver().getId() == currentUser.getId() && m.getStatus() == MessageStatus.SENT)
                .peek(m -> m.setStatus(MessageStatus.READ))
                .toList();

        if (!unreadMessages.isEmpty()) {
            chatMessageRepository.saveAll(unreadMessages);
        }
        return messages.stream()
                .map(chatMessageMapper::toChatMessageResponse)
                .collect(Collectors.toList());
    }

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

            return ConversationResponse.builder()
                    .conversationPartnerId(partner.getId())
                    .conversationPartnerUsername(partner.getUsername())
                    .conversationPartnerName(partner.getFullName() != null ? partner.getFullName() : partner.getUsername())
                    .conversationPartnerAvatar(partner.getImagePath())
                    .lastMessage(msg.getMessage())
                    .lastMessageSender(isSender ? "me" : "other")
                    .timestamp(msg.getSentAt())
                    .unreadCount(unreadCount)
                    .lastMessageStatus(msg.getStatus().toString())
                    .hotelId(msg.getHotel().getId())
                    .hotelName(msg.getHotel().getName())
                    .build();
        }).collect(Collectors.toList());
    }
}