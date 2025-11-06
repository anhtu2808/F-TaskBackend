package com.anhtu.ftaskbackend.service;

import com.anhtu.ftaskbackend.dto.response.notification.NotificationResponse;
import com.anhtu.ftaskbackend.entity.Booking;
import com.anhtu.ftaskbackend.entity.Review;

import java.util.List;

public interface NotificationService {

    /**
     * Gửi notification khi có booking mới được tạo
     */
    void sendBookingCreatedNotification(Booking booking);

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
}