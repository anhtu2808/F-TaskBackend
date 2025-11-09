package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.response.notification.NotificationResponse;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.Partner;
import com.anhtu.ftaskbackend.entity.Review;
import com.anhtu.ftaskbackend.entity.User;

import java.util.List;

public interface NotificationService {

    /**
     * Gửi notification khi có booking mới được tạo (gửi cho partners)
     */
    void sendBookingCreatedNotification(Booking booking);

    /**
     * Gửi notification cho customer khi booking được tạo thành công
     */
    void sendBookingPlacedConfirmationNotification(Booking booking);

    /**
     * Gửi notification khi partner nhận việc
     */
    void sendBookingClaimedNotification(Booking booking);

    /**
     * Gửi notification khi partner hoàn thành công việc
     */
    void sendBookingCompletedNotification(Booking booking);

    /**
     * Gửi notification khi booking bị hủy
     */
    void sendBookingCancelledNotification(Booking booking, String cancelReason);

    /**
     * Gửi notification khi partner nhận được review mới
     */
    void sendReviewReceivedNotification(Review review);

    /**
     * Gửi notification test
     */
    void sendNotification(Long userId);

    /**
     * Lấy danh sách notification của user
     */
    List<NotificationResponse> getUserNotifications(Long userId);

    /**
     * Đánh dấu notification đã đọc
     */
    void markAsRead(Long notificationId);

    /**
     * Đánh dấu tất cả notification của user là đã đọc
     */
    void markAllAsRead(Long userId);

    /**
     * Đếm số lượng notification chưa đọc của user
     */
    Long getUnreadCount(Long userId);

    /**
     * Gửi notification khi partner bắt đầu công việc
     */
    void sendBookingStartedNotification(Booking booking);

    /**
     * Gửi notification khi partner hủy claim booking
     */
    void sendPartnerCancelledClaimNotification(Booking booking, Partner partner);

    /**
     * Gửi notification khi payment thành công
     */
    void sendPaymentSuccessNotification(User user, Double amount, String paymentType, Long bookingId);

    /**
     * Gửi notification khi payment thất bại
     */
    void sendPaymentFailedNotification(User user, Double amount, String paymentType);

    /**
     * Gửi notification khi partner nhận được tiền từ booking đã hoàn thành
     */
    void sendEarningReceivedNotification(User partner, Double amount, Booking booking);

    /**
     * Gửi notification cho customer khi booking sắp tới giờ làm nhưng chưa đủ partner
     */
    void sendInsufficientPartnersNotification(Booking booking);

    /**
     * Gửi notification khi admin cập nhật trạng thái booking
     */
    void sendBookingStatusUpdateNotification(Booking booking, String reason);

    /**
     * Gửi notification khi admin hoàn tiền
     */
    void sendRefundNotification(Booking booking, Double refundAmount, String reason);
}