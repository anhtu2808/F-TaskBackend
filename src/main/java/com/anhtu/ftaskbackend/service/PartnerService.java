package com.anhtu.ftaskbackend.service;


import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;

public interface PartnerService {

    /**
     * Partner nhận (claim) một booking.
     * - Một booking có thể có nhiều partner cùng nhận.
     * - Giới hạn theo số lượng partner tối đa (booking.partnerLimit).
     * - Nếu đủ số lượng partner, chặn claim mới.
     *
     * @param partnerId ID của partner (lấy từ token)
     * @param bookingId ID của booking cần nhận
     * @return BookingResponse với thông tin booking đã cập nhật
     */
    BookingResponse claimBooking(Long partnerId, Long bookingId);

    /**
     * Partner hủy nhận (cancel claim) booking đã claim trước đó.
     *
     * @param partnerId ID partner (từ token)
     * @param bookingId ID booking cần hủy
     * @return BookingResponse với thông tin booking đã cập nhật
     */
    BookingResponse cancelBooking(Long partnerId, Long bookingId);

    /**
     * Partner bắt đầu thực hiện booking (đã claim trước đó)
     *
     * @param partnerId ID partner
     * @param bookingId ID booking
     * @return BookingResponse với thông tin booking đã cập nhật
     */
    BookingResponse startBooking(Long partnerId, Long bookingId);

    /**
     * Partner hoàn tất công việc của booking (đã start trước đó).
     * - Chỉ cho phép khi partner đang ở trạng thái WORKING.
     * - Khi tất cả partner của booking hoàn thành, booking chuyển sang COMPLETED.
     *
     * @param partnerId ID partner (từ token)
     * @param bookingId ID booking cần hoàn tất
     * @return BookingResponse với thông tin booking đã cập nhật
     */
    BookingResponse completeBooking(Long partnerId, Long bookingId);
}
