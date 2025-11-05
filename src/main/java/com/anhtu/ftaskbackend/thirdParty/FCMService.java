package com.anhtu.ftaskbackend.thirdParty;

import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class FCMService {

    public void sendNotificationToDevice(String fcmToken, String title, String body) {
        sendNotificationToDevice(fcmToken, title, body, null);
    }

    public void sendNotificationToDevice(String fcmToken, String title, String body, Map<String, String> data) {
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("FCM token is null or empty");
            return;
        }
        try {
            Message.Builder messageBuilder = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .putData("click_action", "FLUTTER_NOTIFICATION_CLICK")
                    .setToken(fcmToken);

            if (data != null && !data.isEmpty()) {
                for (Map.Entry<String, String> entry : data.entrySet()) {
                    messageBuilder.putData(entry.getKey(), entry.getValue());
                }
            }

            Message message = messageBuilder.build();
            String response = FirebaseMessaging.getInstance().send(message);
            log.info("Successfully sent notification: {}", response);
        } catch (FirebaseMessagingException e) {
            log.error("Error sending notification: {}", e.getMessage(), e);
        }
    }


    public BatchResponse sendNotificationToMultipleDevices(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("Token list is empty");
            return null;
        }

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .addAllTokens(tokens)
                    .build();

            BatchResponse response = FirebaseMessaging.getInstance()
                                                      .sendEachForMulticast(message);

            log.info("Successfully sent {} messages, failed: {}",
                    response.getSuccessCount(), response.getFailureCount());

            if (response.getFailureCount() > 0) {
                List<SendResponse> responses = response.getResponses();
                for (int i = 0; i < responses.size(); i++) {
                    if (!responses.get(i).isSuccessful()) {
                        log.error("Failed to send to token {}: {}",
                                tokens.get(i).substring(0, 10) + "...",
                                responses.get(i).getException().getMessage());
                    }
                }
            }

            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Error sending multicast notification: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Gửi notification với dry run (test mode)
     */
    public BatchResponse sendNotificationToMultipleDevicesDryRun(List<String> tokens, String title, String body) {
        if (tokens == null || tokens.isEmpty()) {
            log.warn("Token list is empty");
            return null;
        }

        try {
            MulticastMessage message = MulticastMessage.builder()
                    .setNotification(Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .addAllTokens(tokens)
                    .build();

            // sendEachForMulticast() với dry run mode
            BatchResponse response = FirebaseMessaging.getInstance()
                                                      .sendEachForMulticast(message, true);

            log.info("[DRY RUN] Would send {} messages, would fail: {}",
                    response.getSuccessCount(), response.getFailureCount());

            return response;
        } catch (FirebaseMessagingException e) {
            log.error("Error in dry run: {}", e.getMessage());
            return null;
        }
    }
}