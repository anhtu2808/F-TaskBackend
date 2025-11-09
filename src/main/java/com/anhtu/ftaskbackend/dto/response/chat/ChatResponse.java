package com.anhtu.ftaskbackend.dto.response.chat;

import com.anhtu.ftaskbackend.dto.response.user.UserResponse;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ChatResponse {

    String roomName;
    String messageContent;
    Long bookingId;
    boolean isRead;
    UserResponse sender;
    UserResponse receiver;

}
