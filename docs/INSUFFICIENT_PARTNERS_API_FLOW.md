# Insufficient Partners Notification & Auto-Cancel API Flow

## Tổng quan
Khi booking sắp tới giờ làm (còn 6 tiếng) nhưng chưa đủ đối tác, hệ thống sẽ:
1. Gửi thông báo cho customer
2. Customer có 1 giờ để phản hồi (hủy hoặc tiếp tục)
3. Nếu không phản hồi sau 1 giờ → tự động hủy booking

---

## 1. Nhận thông báo (Push Notification)

### Notification Type
```
INSUFFICIENT_PARTNERS_WARNING
```

### Notification Data
```json
{
  "type": "INSUFFICIENT_PARTNERS_WARNING",
  "bookingId": "123",
  "currentPartners": "2",
  "requiredPartners": "4",
  "notificationId": "456"
}
```

### Notification Message
```
"Booking [Tên dịch vụ] của bạn sắp tới giờ làm (còn X giờ) nhưng chỉ có Y/Z đối tác. 
Bạn có muốn hủy booking (hoàn tiền đầy đủ) hay tiếp tục với số đối tác hiện có?"
```

---

## 2. Customer phản hồi (Bắt buộc trong 1 giờ)

### API Endpoint
```
POST /bookings/{bookingId}/insufficient-partners-response
```

### Request Body
```json
{
  "cancel": true   // true = hủy booking, false = tiếp tục với partners hiện có
}
```

### Response (200 OK)
```json
{
  "code": 200,
  "message": "Booking cancelled successfully with full refund" 
  // hoặc "Booking will continue with available partners"
}
```

### Business Logic

#### Nếu `cancel = true`:
- ✅ Hoàn tiền đầy đủ (không mất phí)
- ✅ Booking status → `CANCELLED`
- ✅ Gửi thông báo hủy booking

#### Nếu `cancel = false`:
- ✅ Booking tiếp tục với số partners hiện có
- ✅ Tính lại earnings cho từng partner (tổng tiền / số partners hiện tại)
- ✅ Booking status → `PARTIALLY_ACCEPTED` hoặc `FULLY_ACCEPTED`
- ✅ Gửi thông báo xác nhận

---

## 3. Auto-Cancel (Nếu không phản hồi sau 1 giờ)

### Tự động xử lý bởi hệ thống
- Sau 1 giờ kể từ khi gửi thông báo, nếu customer chưa phản hồi
- Hệ thống tự động:
  - ✅ Hủy booking
  - ✅ Hoàn tiền đầy đủ
  - ✅ Gửi thông báo: "Tự động hủy do khách hàng không phản hồi sau 1 giờ"

### Customer nhận notification
```
Type: JOB_CANCELLED (hoặc BOOKING_CANCELLED)
Message: "Booking đã được tự động hủy do không phản hồi sau 1 giờ"
```

---

## 4. Luồng xử lý ở Android Client

### Bước 1: Lắng nghe Push Notification
```kotlin
// Khi nhận notification với type = INSUFFICIENT_PARTNERS_WARNING
if (notificationType == "INSUFFICIENT_PARTNERS_WARNING") {
    val bookingId = data["bookingId"]
    val currentPartners = data["currentPartners"]
    val requiredPartners = data["requiredPartners"]
    
    // Hiển thị dialog/alert cho user
    showInsufficientPartnersDialog(bookingId, currentPartners, requiredPartners)
}
```

### Bước 2: Hiển thị Dialog cho User
```kotlin
fun showInsufficientPartnersDialog(
    bookingId: String,
    currentPartners: Int,
    requiredPartners: Int
) {
    // Dialog với 2 options:
    // 1. "Hủy booking" (hoàn tiền đầy đủ)
    // 2. "Tiếp tục với X đối tác"
    
    // Set timeout: 1 giờ (3600 giây)
    // Nếu không chọn → booking sẽ tự động hủy
}
```

### Bước 3: Gọi API khi user chọn
```kotlin
// Option 1: Hủy booking
POST /bookings/{bookingId}/insufficient-partners-response
Body: { "cancel": true }

// Option 2: Tiếp tục
POST /bookings/{bookingId}/insufficient-partners-response
Body: { "cancel": false }
```

### Bước 4: Xử lý Response
```kotlin
// Nếu thành công:
// - Cập nhật UI (booking status)
// - Hiển thị thông báo thành công
// - Refresh booking list nếu cần

// Nếu lỗi:
// - Hiển thị error message
// - Cho phép user thử lại
```

---

## 5. Lưu ý quan trọng

### ⏰ Timeout
- Customer có **1 giờ** để phản hồi
- Sau 1 giờ → booking tự động hủy
- Nên hiển thị countdown timer trong UI

### 💰 Refund Policy
- **Hủy thủ công** (trong 1 giờ): Hoàn tiền đầy đủ, không mất phí
- **Tự động hủy** (sau 1 giờ): Hoàn tiền đầy đủ, không mất phí
- Khác với hủy booking thông thường (có thể bị phí 30% nếu < 4 giờ)

### 🔔 Notifications
- Customer sẽ nhận notification khi:
  - Booking thiếu partners (cảnh báo)
  - Booking bị hủy (thủ công hoặc tự động)
  - Booking tiếp tục với partners hiện có

### 📊 Booking Status
- `PENDING`: Chưa có partner nào
- `PARTIALLY_ACCEPTED`: Có một số partners nhưng chưa đủ
- `FULLY_ACCEPTED`: Đủ partners
- `CANCELLED`: Đã hủy

---

## 6. Error Handling

### Lỗi thường gặp:
```json
// 400 Bad Request
{
  "code": 400,
  "message": "Booking is already cancelled/completed or in progress"
}

// 401 Unauthorized
{
  "code": 401,
  "message": "Only booking owner can respond"
}

// 404 Not Found
{
  "code": 404,
  "message": "Booking not found"
}
```

---

## 7. Test Cases

### Test Case 1: Customer hủy trong 1 giờ
1. Nhận notification `INSUFFICIENT_PARTNERS_WARNING`
2. Chọn "Hủy booking"
3. ✅ Nhận thông báo hủy thành công
4. ✅ Tiền được hoàn về wallet

### Test Case 2: Customer tiếp tục trong 1 giờ
1. Nhận notification `INSUFFICIENT_PARTNERS_WARNING`
2. Chọn "Tiếp tục với X đối tác"
3. ✅ Booking tiếp tục với status `PARTIALLY_ACCEPTED`
4. ✅ Partners earnings được tính lại

### Test Case 3: Không phản hồi sau 1 giờ
1. Nhận notification `INSUFFICIENT_PARTNERS_WARNING`
2. Không làm gì trong 1 giờ
3. ✅ Booking tự động hủy
4. ✅ Nhận notification "Tự động hủy"
5. ✅ Tiền được hoàn về wallet

---

## 8. API Endpoints Summary

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| POST | `/bookings/{id}/insufficient-partners-response` | Customer phản hồi (hủy/tiếp tục) |
| GET | `/bookings/{id}` | Lấy thông tin booking (để check status) |
| GET | `/notifications` | Lấy danh sách notifications |

---

## 9. Sample Code (Kotlin)

```kotlin
// Service Interface
interface BookingService {
    @POST("bookings/{bookingId}/insufficient-partners-response")
    suspend fun respondToInsufficientPartners(
        @Path("bookingId") bookingId: Long,
        @Body request: InsufficientPartnersResponseRequest
    ): ApiResponse<Unit>
}

// Request Model
data class InsufficientPartnersResponseRequest(
    val cancel: Boolean
)

// Usage
suspend fun handleInsufficientPartners(
    bookingId: Long,
    cancel: Boolean
) {
    try {
        val response = bookingService.respondToInsufficientPartners(
            bookingId,
            InsufficientPartnersResponseRequest(cancel)
        )
        
        if (response.code == 200) {
            // Success - update UI
            showSuccessMessage(response.message)
            refreshBookingList()
        }
    } catch (e: Exception) {
        // Handle error
        showErrorMessage(e.message)
    }
}
```

---

**Tài liệu này đủ để implement ở Android client. Nếu cần thêm chi tiết, vui lòng hỏi!**

