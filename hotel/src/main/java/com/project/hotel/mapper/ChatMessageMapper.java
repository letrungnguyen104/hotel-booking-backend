package com.project.hotel.mapper;

import com.project.hotel.dto.response.ChatMessageResponse;
import com.project.hotel.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ChatMessageMapper {
    @Mapping(source = "sender.id", target = "senderId")
    @Mapping(source = "receiver.id", target = "receiverId")
    @Mapping(source = "sender.username", target = "senderUsername")
    @Mapping(source = "receiver.username", target = "receiverUsername")
    @Mapping(source = "sender.fullName", target = "senderFullName")
    @Mapping(source = "receiver.fullName", target = "receiverFullName")
    @Mapping(source = "hotel.id", target = "hotelId")
    ChatMessageResponse toChatMessageResponse(ChatMessage chatMessage);
}
