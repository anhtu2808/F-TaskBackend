package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.request.chat.ChatRequest;
import com.anhtu.ftaskbackend.dto.response.chat.ChatResponse;
import com.google.cloud.firestore.Firestore;

import java.util.List;

public interface ChatService {

    Firestore getFirestore();
    void saveMessage(String id, Object message);
    ChatResponse sendMessage(Long id, ChatRequest request);
    List<ChatResponse> getChatHistoryFromFirestore(Long bookingId, Long receiverId);
    List<ChatResponse> getAllChatThreadsOfCurrentUser();
}
