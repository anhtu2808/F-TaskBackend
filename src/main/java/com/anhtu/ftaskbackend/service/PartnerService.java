package com.anhtu.ftaskbackend.service;


import com.anhtu.ftaskbackend.dto.request.partner.RegisterDistrictsRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerFilterRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerStatusUpdateRequest;
import com.anhtu.ftaskbackend.dto.request.admin.AdminPartnerDistrictsRequest;
import com.anhtu.ftaskbackend.dto.response.booking.BookingResponse;
import com.anhtu.ftaskbackend.dto.response.district.DistrictResponse;
import com.anhtu.ftaskbackend.dto.response.partner.PartnerResponse;
import org.springframework.data.domain.Page;

import java.util.List;

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
     * Partner bắt đầu thực hiện booking bằng QR code token
     *
     * @param qrToken JWT token từ QR code
     * @return BookingResponse với thông tin booking đã cập nhật
     */
    BookingResponse startBookingByQR(String qrToken);

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

    /**
     * Partner đăng ký/cập nhật danh sách quận hoạt động
     *
     * @param partnerId ID partner (từ token)
     * @param request Danh sách district IDs cần đăng ký
     */
    void registerDistricts(Long partnerId, RegisterDistrictsRequest request);

    /**
     * Lấy danh sách quận đã đăng ký của partner
     *
     * @param partnerId ID partner (từ token)
     * @return Danh sách DistrictResponse
     */
    List<DistrictResponse> getRegisteredDistricts(Long partnerId);

    /**
     * Lấy tất cả các quận có sẵn để partner chọn
     *
     * @return Danh sách tất cả DistrictResponse
     */
    List<DistrictResponse> getAllDistricts();
    
    // Admin methods
    Page<PartnerResponse> getAllPartnersForAdmin(AdminPartnerFilterRequest filter);
    PartnerResponse adminGetPartnerById(Long partnerId);
    void adminUpdatePartnerStatus(Long partnerId, AdminPartnerStatusUpdateRequest request);
    void adminUpdatePartnerDistricts(Long partnerId, AdminPartnerDistrictsRequest request);
    Page<BookingResponse> adminGetPartnerBookings(Long partnerId, Integer page, Integer size);
}
