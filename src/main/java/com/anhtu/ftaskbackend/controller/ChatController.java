package com.anhtu.ftaskbackend.controller;

import com.anhtu.ftaskbackend.common.ApiResponse;
import com.anhtu.ftaskbackend.configuration.FireStoreChatInitializer;
import com.anhtu.ftaskbackend.dto.response.chat.ChatResponse;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.service.ChatService;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PRIVATE;

@RestController
@RequestMapping("/chat")
@FieldDefaults(level = PRIVATE)
@Tag(name = "Chat", description = "Chat management APIs")
public class ChatController {

    @Autowired
    ChatService chatService;

    @GetMapping("/firestore")
    public String testFirestore() throws Exception {
        Firestore db = FirestoreClient.getFirestore(FirebaseApp.getInstance(FireStoreChatInitializer.FIRESTORE_APP_NAME));
        db.collection("test").document("hello").set(Map.of("message", "Firestore is connected!"));
        return "Data written to Firestore successfully!";
    }

    @GetMapping("/firebase/bookings/{bookingId}/receiver/{receiverId}")
    public ApiResponse<List<ChatResponse>> getChatHistoryFromFirebase(
            @PathVariable Long bookingId,
            @PathVariable Long receiverId
    ) {
        return ApiResponse.<List<ChatResponse>>builder()
                .code(200)
                .message("Get chat history from Firestore successfully")
                .result(chatService.getChatHistoryFromFirestore(bookingId, receiverId))
                .build();
    }

    @GetMapping("/firebase/threads")
    public ApiResponse<List<ChatResponse>> getAllChatThreadsOfCurrentUser() {
        return ApiResponse.<List<ChatResponse>>builder()
                .code(200)
                .message("Get all chat threads of current user successfully")
                .result(chatService.getAllChatThreadsOfCurrentUser())
                .build();
    }

}

