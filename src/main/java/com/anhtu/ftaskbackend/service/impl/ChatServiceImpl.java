package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.configuration.FireStoreChatInitializer;
import com.anhtu.ftaskbackend.dto.request.chat.ChatRequest;
import com.anhtu.ftaskbackend.dto.response.chat.ChatResponse;
import com.anhtu.ftaskbackend.entity.ChatMessage;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.helper.JWTHelper;
import com.anhtu.ftaskbackend.mapper.ChatMapper;
import com.anhtu.ftaskbackend.mapper.UserMapper;
import com.anhtu.ftaskbackend.repository.BookingRepository;
import com.anhtu.ftaskbackend.repository.ChatRepository;
import com.anhtu.ftaskbackend.repository.UserRepository;
import com.anhtu.ftaskbackend.service.ChatService;
import com.google.cloud.Timestamp;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.SetOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.cloud.FirestoreClient;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ChatServiceImpl implements ChatService {
    ChatRepository chatMessageRepository;
    BookingRepository bookingRepository;
    UserRepository userRepository;
    ChatMapper chatMapper;
    UserMapper userMapper;

    /**
     * ✅ Lấy Firestore instance từ app tên "firestoreApp"
     */
    @Override
    public Firestore getFirestore() {
        return FirestoreClient.getFirestore(
                FirebaseApp.getInstance(FireStoreChatInitializer.FIRESTORE_APP_NAME)
        );
    }

    /**
     * ✅ Lưu một object bất kỳ vào Firestore
     */
    @Override
    public void saveMessage(String id, Object message) {
        getFirestore()
                .collection("chat_messages")
                .document(id)
                .set(message);
    }

    @Override
    public ChatResponse sendMessage(Long bookingId, ChatRequest request) {
        Long userId = JWTHelper.getCurrentUserId();

        // 1️⃣ Tạo đối tượng ChatMessage và lưu MySQL
        ChatMessage message = ChatMessage.builder()
                .booking(bookingRepository.findById(bookingId)
                        .orElseThrow(() -> new AppException(ErrorCode.BookingNotFound)))
                .sender(userRepository.findById(userId)
                        .orElseThrow(() -> new AppException(ErrorCode.UserNotFound)))
                .receiver(userRepository.findById(request.getReceiverId())
                        .orElseThrow(() -> new AppException(ErrorCode.UserNotFound)))
                .messageContent(request.getMessage())
                .build();

        chatMessageRepository.save(message);

        // 2️⃣ Đồng bộ Firestore
        try {
            Firestore db = getFirestore();

            Map<String, Object> data = new HashMap<>();
            data.put("messageId", message.getId());
            data.put("senderId", message.getSender().getId());
            data.put("receiverId", message.getReceiver().getId());
            data.put("content", message.getMessageContent());
            data.put("isRead", message.getIsRead());
            data.put("createdAt", message.getCreateAt() != null
                    ? Timestamp.of(Date.from(message.getCreateAt().atZone(java.time.ZoneId.systemDefault()).toInstant()))
                    : Timestamp.now());

            // Ghi message vào subcollection
            db.collection("chat_rooms")
                    .document("booking_" + message.getBooking().getId())
                    .collection("messages")
                    .document(String.valueOf(message.getId()))
                    .set(data);

            // Cập nhật last message
            db.collection("chat_rooms")
                    .document("booking_" + message.getBooking().getId())
                    .set(Map.of(
                            "last_message", message.getMessageContent(),
                            "updatedAt", Timestamp.now()
                    ), SetOptions.merge());

            System.out.println("Message synced to Firestore");
        } catch (Exception e) {
            throw new RuntimeException("Failed to sync to Firestore", e);
        }

        // 3️⃣ Trả về response DTO
        ChatResponse response = chatMapper.toChatResponse(message);
        response.setBookingId(bookingId);
        return response;
    }

    @Override
    public List<ChatResponse> getChatHistoryFromFirestore(Long bookingId, Long receiverId) {
        try {
            Long userId = JWTHelper.getCurrentUserId();
            Firestore db = getFirestore();

            var snapshot = db.collection("chat_rooms")
                    .document("booking_" + bookingId)
                    .collection("messages")
                    .orderBy("createdAt")
                    .get()
                    .get();

            List<ChatResponse> responses = new ArrayList<>();

            for (var doc : snapshot.getDocuments()) {
                Map<String, Object> data = doc.getData();

                Long senderId = ((Number) data.get("senderId")).longValue();
                Long recId = ((Number) data.get("receiverId")).longValue();

                // ✅ Chỉ lấy tin nhắn giữa current user ↔ receiverId
                boolean isMatched = (senderId.equals(userId) && recId.equals(receiverId))
                        || (senderId.equals(receiverId) && recId.equals(userId));
                if (!isMatched) continue;

                ChatResponse res = new ChatResponse();
                res.setBookingId(bookingId);
                res.setMessageContent((String) data.get("content"));
                res.setRead(Boolean.TRUE.equals(data.get("isRead")));
                res.setRoomName("booking_" + bookingId);

                // optional: lấy sender/receiver info từ DB
                res.setSender(userMapper.toUserResponse(
                        userRepository.findById(senderId).orElse(null)));
                res.setReceiver(userMapper.toUserResponse(
                        userRepository.findById(recId).orElse(null)));

                responses.add(res);
            }

            return responses;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch chat history from Firestore", e);
        }
    }

    @Override
    public List<ChatResponse> getAllChatThreadsOfCurrentUser() {
        try {
            Long userId = JWTHelper.getCurrentUserId();
            Firestore db = getFirestore();

            var chatRoomsSnapshot = db.collection("chat_rooms").get().get();

            // Dùng map key = bookingId_receiverId để gom các thread
            Map<String, ChatResponse> latestMessageByThread = new HashMap<>();

            for (var room : chatRoomsSnapshot.getDocuments()) {
                String roomId = room.getId(); // ví dụ: booking_3
                Long bookingId = Long.parseLong(roomId.replace("booking_", ""));

                var messagesSnapshot = db.collection("chat_rooms")
                        .document(roomId)
                        .collection("messages")
                        .orderBy("createdAt", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .get()
                        .get();

                for (var doc : messagesSnapshot.getDocuments()) {
                    Map<String, Object> data = doc.getData();
                    if (data == null) continue;

                    Long senderId = ((Number) data.get("senderId")).longValue();
                    Long receiverId = ((Number) data.get("receiverId")).longValue();

                    // ✅ chỉ lấy các tin nhắn có liên quan đến current user
                    if (!senderId.equals(userId) && !receiverId.equals(userId)) continue;

                    // ✅ Xác định ai là đối tác trò chuyện
                    Long chatPartnerId = senderId.equals(userId) ? receiverId : senderId;
                    String key = bookingId + "_" + chatPartnerId;

                    // chỉ lấy tin nhắn mới nhất cho mỗi (bookingId + receiverId)
                    if (!latestMessageByThread.containsKey(key)) {
                        ChatResponse res = new ChatResponse();
                        res.setBookingId(bookingId);
                        res.setRoomName(roomId);
                        res.setMessageContent((String) data.get("content"));
                        res.setRead(Boolean.TRUE.equals(data.get("isRead")));

                        res.setSender(userMapper.toUserResponse(
                                userRepository.findById(senderId).orElse(null)));
                        res.setReceiver(userMapper.toUserResponse(
                                userRepository.findById(receiverId).orElse(null)));

                        latestMessageByThread.put(key, res);
                    }
                }
            }

            return new ArrayList<>(latestMessageByThread.values());

        } catch (Exception e) {
            throw new RuntimeException("❌ Failed to fetch all chat threads from Firestore", e);
        }
    }
}

