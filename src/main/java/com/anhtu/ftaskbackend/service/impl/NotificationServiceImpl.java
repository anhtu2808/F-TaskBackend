package com.anhtu.ftaskbackend.service.impl;

import com.anhtu.ftaskbackend.dto.response.notification.NotificationResponse;
import com.anhtu.ftaskbackend.entity.*;
import com.anhtu.ftaskbackend.enums.BookingPartnerStatus;
import com.anhtu.ftaskbackend.enums.BookingStatus;
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
    private final BookingRepository bookingRepository;
    private final BookingPartnerRepository bookingPartnerRepository;
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
    public void sendBookingPlacedConfirmationNotification(Booking booking) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Customer {} has no FCM token, skipping notification", customer.getId());
            return;
        }

        String message;
        if (booking.getStatus() == BookingStatus.WAITING_FOR_PAYMENT) {
            message = String.format("Booking %s của bạn đã được tạo thành công. Vui lòng thanh toán để hệ thống bắt đầu tìm kiếm đối tác.",
                    booking.getVariant().getServiceCatalog().getName());
        } else {
            message = String.format("Booking %s của bạn đã được tạo thành công. Hệ thống đang tìm kiếm đối tác phù hợp.",
                    booking.getVariant().getServiceCatalog().getName());
        }

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.BOOKING_CREATED)
                .title("Đặt booking thành công")
                .message(message)
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.BOOKING_CREATED.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent booking placed confirmation notification to customer {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to customer {}: {}", customer.getId(), e.getMessage());
        }
    }

    @Override
    public void sendBookingClaimedNotification(Booking booking) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Customer {} has no FCM token, skipping notification", customer.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.JOB_ACCEPTED)
                .title("Booking đã được nhận")
                .message(String.format("Công việc %s của bạn đã được partner nhận.",
                        booking.getVariant().getServiceCatalog().getName()))
                .build();
        
        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.JOB_ACCEPTED.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent booking claimed notification to customer {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to customer {}: {}", customer.getId(), e.getMessage());
        }
    }

    @Override
    public void sendNotification(Long userId) {
        User user = userRepository.findById(userId)
                                  .orElseThrow(() -> new AppException(ErrorCode.UserNotFound));

        String fcmToken = user.getFcmToken();
        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("User {} has no FCM token, skipping test notification", userId);
            return;
        }

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
                .type(NotificationType.REVIEW_RECEIVED)
                .title("Bạn nhận được đánh giá mới")
                .message(String.format("Bạn nhận được đánh giá %d sao từ khách hàng. %s",
                        review.getRating(),
                        review.getDescription() != null ? review.getDescription() : ""))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.REVIEW_RECEIVED.name());
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

    @Override
    public void sendBookingStartedNotification(Booking booking) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Customer {} has no FCM token, skipping notification", customer.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.JOB_STARTED)
                .title("Công việc đã bắt đầu")
                .message(String.format("Công việc %s của bạn đã bắt đầu. Partner đang thực hiện công việc.",
                        booking.getVariant().getServiceCatalog().getName()))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.JOB_STARTED.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent booking started notification to customer {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to customer {}: {}", customer.getId(), e.getMessage());
        }
    }

    @Override
    public void sendPartnerCancelledClaimNotification(Booking booking, Partner partner) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Customer {} has no FCM token, skipping notification", customer.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.PARTNER_CANCELLED_CLAIM)
                .title("Partner đã hủy nhận việc")
                .message(String.format("Một partner đã hủy nhận công việc %s. Booking đang chờ partner mới.",
                        booking.getVariant().getServiceCatalog().getName()))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.PARTNER_CANCELLED_CLAIM.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent partner cancelled claim notification to customer {}", customer.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to customer {}: {}", customer.getId(), e.getMessage());
        }
    }

    @Override
    public void sendPaymentSuccessNotification(User user, Double amount, String paymentType, Long bookingId) {
        String fcmToken = user.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("User {} has no FCM token, skipping notification", user.getId());
            return;
        }

        String title = "";
        String message = "";

        switch (paymentType) {
            case "TOPUP" -> {
                title = "Nạp tiền thành công";
                message = String.format("Bạn đã nạp thành công %.0f VNĐ vào ví.", amount);
            }
            case "PAYMENT" -> {
                title = "Thanh toán thành công";
                message = String.format("Bạn đã thanh toán thành công %.0f VNĐ cho booking.", amount);
            }
            case "WITHDRAWAL" -> {
                title = "Rút tiền thành công";
                message = String.format("Bạn đã rút thành công %.0f VNĐ từ ví.", amount);
            }
            default -> {
                title = "Giao dịch thành công";
                message = String.format("Giao dịch %.0f VNĐ đã được thực hiện thành công.", amount);
            }
        }

        Notification.NotificationBuilder notificationBuilder = Notification.builder()
                .user(user)
                .type(NotificationType.PAYMENT_SUCCESS)
                .title(title)
                .message(message);

        if (bookingId != null) {
            bookingRepository.findById(bookingId).ifPresent(notificationBuilder::booking);
        }

        Notification notification = notificationBuilder.build();
        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.PAYMENT_SUCCESS.name());
        data.put("paymentType", paymentType);
        data.put("amount", String.valueOf(amount));
        data.put("notificationId", String.valueOf(notification.getId()));
        if (bookingId != null) {
            data.put("bookingId", String.valueOf(bookingId));
        }

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent payment success notification to user {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", user.getId(), e.getMessage());
        }
    }

    @Override
    public void sendPaymentFailedNotification(User user, Double amount, String paymentType) {
        String fcmToken = user.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("User {} has no FCM token, skipping notification", user.getId());
            return;
        }

        String title = "";
        String message = "";

        switch (paymentType) {
            case "TOPUP" -> {
                title = "Nạp tiền thất bại";
                message = String.format("Giao dịch nạp tiền %.0f VNĐ đã thất bại. Vui lòng thử lại.", amount);
            }
            case "PAYMENT" -> {
                title = "Thanh toán thất bại";
                message = String.format("Giao dịch thanh toán %.0f VNĐ đã thất bại. Vui lòng thử lại.", amount);
            }
            case "WITHDRAWAL" -> {
                title = "Rút tiền thất bại";
                message = String.format("Giao dịch rút tiền %.0f VNĐ đã thất bại. Vui lòng thử lại.", amount);
            }
            default -> {
                title = "Giao dịch thất bại";
                message = String.format("Giao dịch %.0f VNĐ đã thất bại. Vui lòng thử lại.", amount);
            }
        }

        Notification notification = Notification.builder()
                .user(user)
                .type(NotificationType.PAYMENT_FAILED)
                .title(title)
                .message(message)
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.PAYMENT_FAILED.name());
        data.put("paymentType", paymentType);
        data.put("amount", String.valueOf(amount));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent payment failed notification to user {}", user.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to user {}: {}", user.getId(), e.getMessage());
        }
    }

    @Override
    public void sendEarningReceivedNotification(User partner, Double amount, Booking booking) {
        String fcmToken = partner.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Partner {} has no FCM token, skipping notification", partner.getId());
            return;
        }

        Notification notification = Notification.builder()
                .user(partner)
                .booking(booking)
                .type(NotificationType.EARNING_RECEIVED)
                .title("Bạn đã nhận được tiền")
                .message(String.format("Bạn đã nhận được %.0f VNĐ từ booking %s đã hoàn thành.",
                        amount,
                        booking.getVariant().getServiceCatalog().getName()))
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.EARNING_RECEIVED.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("amount", String.valueOf(amount));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent earning received notification to partner {}", partner.getId());
        } catch (Exception e) {
            log.error("Failed to send notification to partner {}: {}", partner.getId(), e.getMessage());
        }
    }

    @Override
    public void sendInsufficientPartnersNotification(Booking booking) {
        User customer = booking.getCustomer().getUser();
        String fcmToken = customer.getFcmToken();

        if (fcmToken == null || fcmToken.isEmpty()) {
            log.warn("Customer {} has no FCM token, skipping insufficient partners notification", customer.getId());
            return;
        }

        // Count current active partners (JOINED or WORKING status)
        long currentPartners = bookingPartnerRepository.countByBookingAndStatusIn(
                booking,
                List.of(BookingPartnerStatus.JOINED, BookingPartnerStatus.WORKING)
        );
        int requiredPartners = booking.getRequiredPartners();

        String title = "Cảnh báo: Booking chưa đủ đối tác";
        String message = String.format(
                "Booking %s của bạn sắp tới giờ làm (còn %d giờ) nhưng chỉ có %d/%d đối tác. " +
                "Bạn có muốn hủy booking (hoàn tiền đầy đủ) hay tiếp tục với số đối tác hiện có?",
                booking.getVariant().getServiceCatalog().getName(),
                java.time.Duration.between(java.time.LocalDateTime.now(), booking.getStartAt()).toHours(),
                currentPartners,
                requiredPartners
        );

        Notification notification = Notification.builder()
                .user(customer)
                .booking(booking)
                .type(NotificationType.INSUFFICIENT_PARTNERS_WARNING)
                .title(title)
                .message(message)
                .build();

        notificationRepository.save(notification);

        Map<String, String> data = new HashMap<>();
        data.put("type", NotificationType.INSUFFICIENT_PARTNERS_WARNING.name());
        data.put("bookingId", String.valueOf(booking.getId()));
        data.put("currentPartners", String.valueOf(currentPartners));
        data.put("requiredPartners", String.valueOf(requiredPartners));
        data.put("notificationId", String.valueOf(notification.getId()));

        try {
            fcmService.sendNotificationToDevice(
                    fcmToken,
                    notification.getTitle(),
                    notification.getMessage(),
                    data
            );
            log.info("Sent insufficient partners notification to customer {} for booking {}", 
                    customer.getId(), booking.getId());
        } catch (Exception e) {
            log.error("Failed to send insufficient partners notification to customer {}: {}", 
                    customer.getId(), e.getMessage());
        }
    }

}
