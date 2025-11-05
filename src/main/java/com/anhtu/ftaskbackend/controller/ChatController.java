//package com.anhtu.ftaskbackend.controller;
//
//import com.anhtu.ftaskbackend.common.ApiResponse;
//import com.anhtu.ftaskbackend.dto.request.chat.ChatMessageRequest;
//import com.anhtu.ftaskbackend.dto.response.chat.ChatMessageResponse;
//import com.anhtu.ftaskbackend.dto.response.chat.ChatRoomResponse;
//import com.anhtu.ftaskbackend.helper.JWTHelper;
//import com.anhtu.ftaskbackend.service.ChatService;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import lombok.RequiredArgsConstructor;
//import lombok.experimental.FieldDefaults;
//import org.springframework.http.HttpStatus;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//import static lombok.AccessLevel.PRIVATE;
//
//@RestController
//@RequestMapping("/chat")
//@RequiredArgsConstructor
//@FieldDefaults(level = PRIVATE, makeFinal = true)
//@Tag(name = "Chat", description = "Chat management APIs")
//public class ChatController {
//
//    ChatService chatService;
//
//    @PostMapping("/messages")
//    @Operation(summary = "Send a chat message")
//    public ApiResponse<ChatMessageResponse> sendMessage(@Valid @RequestBody ChatMessageRequest request) {
//        Long senderId = JWTHelper.getCurrentUserId();
//        ChatMessageResponse response = chatService.sendMessage(senderId, request);
//        return ApiResponse.<ChatMessageResponse>builder()
//                .code(HttpStatus.CREATED.value())
//                .message("Message sent successfully")
//                .result(response)
//                .build();
//    }
//
//    @GetMapping("/bookings/{bookingId}/messages")
//    @Operation(summary = "Get chat history of a booking")
//    public ApiResponse<List<ChatMessageResponse>> getChatHistory(@PathVariable Long bookingId) {
//        Long userId = JWTHelper.getCurrentUserId();
//        List<ChatMessageResponse> messages = chatService.getChatHistory(userId, bookingId);
//        return ApiResponse.<List<ChatMessageResponse>>builder()
//                .message("Get chat history successfully")
//                .result(messages)
//                .build();
//    }
//
//    @GetMapping("/rooms")
//    @Operation(summary = "Get all chat rooms of current user")
//    public ApiResponse<List<ChatRoomResponse>> getChatRooms() {
//        Long userId = JWTHelper.getCurrentUserId();
//        List<ChatRoomResponse> rooms = chatService.getChatRooms(userId);
//        return ApiResponse.<List<ChatRoomResponse>>builder()
//                .message("Get chat rooms successfully")
//                .result(rooms)
//                .build();
//    }
//
//    @PutMapping("/bookings/{bookingId}/read")
//    @Operation(summary = "Mark all messages as read in a booking")
//    public ApiResponse<Void> markAsRead(@PathVariable Long bookingId) {
//        Long userId = JWTHelper.getCurrentUserId();
//        chatService.markMessagesAsRead(userId, bookingId);
//        return ApiResponse.<Void>builder()
//                .message("Messages marked as read successfully")
//                .build();
//    }
//
//    @GetMapping("/unread-count")
//    @Operation(summary = "Get total unread message count")
//    public ApiResponse<Long> getUnreadCount() {
//        Long userId = JWTHelper.getCurrentUserId();
//        Long count = chatService.getUnreadCount(userId);
//        return ApiResponse.<Long>builder()
//                .message("Get unread count successfully")
//                .result(count)
//                .build();
//    }
//}
//
