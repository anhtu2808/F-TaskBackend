package com.anhtu.ftaskbackend.mapper;

import com.anhtu.ftaskbackend.dto.response.chat.ChatResponse;
import com.anhtu.ftaskbackend.entity.ChatMessage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChatMapper {

    @Mapping(source = "messageContent", target = "messageContent")
    ChatResponse toChatResponse(ChatMessage chatMessage);

}
