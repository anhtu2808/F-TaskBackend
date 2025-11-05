package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.response.notification.NotificationResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.enums.NotificationType;
import com.anhtu.ftaskbackend.exception.AppException;
import com.anhtu.ftaskbackend.exception.ErrorCode;
import com.anhtu.ftaskbackend.mapper.NotificationMapper;
import com.anhtu.ftaskbackend.repository.*;
import com.anhtu.ftaskbackend.service.NotificationService;
import com.anhtu.ftaskbackend.thirdParty.FCMService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final PartnerRepository partnerRepository;
    private final FCMService fcmService;
    private final NotificationMapper notificationMapper;

    @Override
    public void sendBookingCreatedNotification(Booking booking) {
        String bookingDistrict = booking.getAddress().getDistrict();
        List<Partner> eligiblePartners = partnerRepository.findAvailablePartnersByDistrict(bookingDistrict);

        log.info("Found {} eligible partners for booking {} in district {}", 
                 eligiblePartners.size(), booking.getId(), bookingDistrict);

        for (Partner partner : eligiblePartners) {
            User partnerUser = partner.getUser();
            String fcmToken = partnerUser.getFcmToken();

            if (fcmToken == null || fcmToken.isEmpty()) {
                log.warn("Partner {} has no FCM token, skipping notification", partner.getId());
                continue;
            }

            Notification notification = Notification.builder()
                    .user(partnerUser)
                    .booking(booking)
                    .type(NotificationType.NEW_JOB_AVAILABLE)
                    .title("Công việc mới trong khu vực của bạn")
                    .message(String.format("Có công việc %s mới tại %s. Giá: %.0f VNĐ",
                            booking.getVariant().getServiceCatalog().getName(),
                            bookingDistrict,
                            booking.getTotalPrice()))
                    .build();

            notificationRepository.save(notification);

            Map<String, String> data = new HashMap<>();
            data.put("type", NotificationType.NEW_JOB_AVAILABLE.name());
            data.put("bookingId", String.valueOf(booking.getId()));
            data.put("notificationId", String.valueOf(notification.getId()));

            try {
                fcmService.sendNotificationToDevice(
                        fcmToken,
                        notification.getTitle(),
                        notification.getMessage(),
                        data
                );
                log.info("Sent booking created notification to partner {}", partner.getId());
            } catch (Exception e) {
                log.error("Failed to send notification to partner {}: {}", partner.getId(), e.getMessage());
            }
        }
    }

    @Override
    public void sendBookingClaimedNotification(Booking booking) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.JOB_ACCEPTED)
                .title("Booking Claimed")
                .message("Your booking for service" + booking.getVariant().getServiceCatalog().getName()+ " has been claimed.")
                .build();
        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.JOB_ACCEPTED.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));

        fcmService.sendNotificationToDevice(
                fcmToken,
                notification.getTitle(),
                notification.getMessage(),
                data
        );
    }

    @Override
    public void sendNotification(Long userId, String fcmToken) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));

        Notification notification = Notification.builder()
                .user(user)
                .title("Test Notification")
                .message("This is a test notification from FTask backend.")
                .type(NotificationType.PAYMENT_CREATED)
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.PAYMENT_CREATED.toString());
        data.put("notificationId", String.valueOf(notification.getId()));

        fcmService.sendNotificationToDevice(
                fcmToken,
                notification.getTitle(),
                notification.getMessage(),
                data
        );

        log.info("Sent test notification to user {}", userId);
    }

    @Override
    public List<NotificationResponse> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreateAtDesc(userId)
                .stream()
                .map(notificationMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                                                          .orElseThrow(() -> new AppException(ErrorCode.NotificationNotFound));
        notification.setIsRead(true);
        notificationRepository.save(notification);
        log.info("Marked notification {} as read", notificationId);
    }

    @Override
    public void sendBookingCompletedNotification(Booking booking) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Customer {} has no FCM token, skipping notification", customer.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.JOB_COMPLETED)
                .title("Công việc đã hoàn thành")
                .message(String.format("Công việc %s của bạn đã được hoàn thành. Vui lòng kiểm tra và đánh giá.",
                        booking.getVariant().getServiceCatalog().getName()))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.JOB_COMPLETED.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent booking completed notification to customer {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to customer {}: {}", customer.getId(), e.getMessage());
        }
    }

    @Override
    public void sendBookingCancelledNotification(Booking booking, String cancelReason) {
        // Gửi notification cho customer
        User customer = booking.getCustomer().getUser();
        sendCancellationNotification(customer, booking, cancelReason);

        // Gửi notification cho các partner đã claim booking (nếu có)
        booking.getPartners().forEach(bookingPartner -> {
            User partner = bookingPartner.getPartner().getUser();
            sendCancellationNotification(partner, booking, cancelReason);
        });
    }

    private void sendCancellationNotification(User user, Booking booking, String cancelReason) {
        String fcmToken = user.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("User {} has no FCM token, skipping cancellation notification", user.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(user)
                .booking(booking)
                .type(NotificationType.JOB_COMPLETED) // Can add JOB_CANCELLED to enum if needed
                .title("Booking đã bị hủy")
                .message(String.format("Booking %s đã bị hủy. Lý do: %s",
                        booking.getVariant().getServiceCatalog().getName(),
                        cancelReason != null ? cancelReason : "Không có lý do"))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", "BOOKING_CANCELLED");
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));
        data.put("cancelReason", cancelReason != null ? cancelReason : "");

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent booking cancelled notification to user {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send cancellation notification to user {}: {}", user.getId(), e.getMessage());
        }
    }

    @Override
    public void sendReviewReceivedNotification(Review review) {
        User partner = review.getPartner().getUser();
        String fcmToken = partner.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Partner {} has no FCM token, skipping notification", partner.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(partner)
                .booking(review.getBooking())
                .type(NotificationType.JOB_COMPLETED) // Can add REVIEW_RECEIVED to enum if needed
                .title("Bạn nhận được đánh giá mới")
                .message(String.format("Bạn nhận được đánh giá %d sao từ khách hàng. %s",
                        review.getRating(),
                        review.getDescription() != null ? review.getDescription() : ""))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", "REVIEW_RECEIVED");
        data.put("reviewId", String.valueOf(review.getId()));
        data.put("bookingId", String.valueOf(review.getBooking().getId()));
        data.put("notificationId", String.valueOf(notification.getId()));
        data.put("rating", String.valueOf(review.getRating()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent review notification to partner {}", partner.getId());
        } catch (Exception e) {
            log.error("Failed to send review notification to partner {}: {}", partner.getId(), e.getMessage());
        }
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        notificationRepository.markAllAsReadByUserId(userId);
        log.info("Marked all notifications as read for user {}", userId);
    }

    @Override
    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByUserIdAndIsReadFalse(userId);
    }


}
