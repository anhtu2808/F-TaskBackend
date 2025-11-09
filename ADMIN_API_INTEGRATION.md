# Admin API Integration Guide

## Overview

This document provides comprehensive integration guidelines for the FTask Admin API system. All admin endpoints are prefixed with `/admin` and provide full CRUD operations with advanced filtering and pagination capabilities.

## Base Configuration

- **Base URL**: `http://localhost:8080/api/v1`
- **Content-Type**: `application/json`
- **Authentication**: No authentication required (as per requirements)

## Common Response Format

All API responses follow this consistent format:

```json
{
  "code": 200,
  "message": "Success message",
  "result": {
    // Response data here
  }
}
```

## Pagination Format

All list endpoints support pagination with this format:

```json
{
  "content": [...],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 20,
    "sort": {
      "sorted": true,
      "direction": "DESC",
      "property": "createdAt"
    }
  },
  "totalElements": 100,
  "totalPages": 5,
  "first": true,
  "last": false,
  "numberOfElements": 20
}
```

## 1. Dashboard APIs

### Get Overall Statistics
```http
GET /admin/dashboard/stats
```

**Response:**
```json
{
  "code": 200,
  "message": "Dashboard statistics retrieved successfully",
  "result": {
    "totalUsers": 1250,
    "totalCustomers": 800,
    "totalPartners": 450,
    "totalBookings": 5000,
    "totalCompletedBookings": 4200,
    "totalCancelledBookings": 300,
    "totalPendingBookings": 500,
    "totalServiceCatalogs": 25,
    "totalServiceVariants": 150,
    "totalReviews": 3800,
    "totalRevenue": 2500000.0,
    "totalPlatformFee": 500000.0,
    "averageRating": 4.2
  }
}
```

### Get Revenue Statistics
```http
GET /admin/dashboard/revenue?fromDate=2024-01-01&toDate=2024-01-31
```

**Response:**
```json
{
  "code": 200,
  "message": "Revenue statistics retrieved successfully",
  "result": {
    "totalRevenue": 500000.0,
    "totalPlatformFee": 100000.0,
    "fromDate": "2024-01-01",
    "toDate": "2024-01-31",
    "dailyStats": [
      {
        "date": "2024-01-01",
        "revenue": 15000.0,
        "platformFee": 3000.0,
        "bookingCount": 25
      }
    ]
  }
}
```

### Get Booking Trends
```http
GET /admin/dashboard/bookings-trend?fromDate=2024-01-01&toDate=2024-01-31&period=DAILY
```

**Response:**
```json
{
  "code": 200,
  "message": "Booking trends retrieved successfully",
  "result": {
    "fromDate": "2024-01-01",
    "toDate": "2024-01-31",
    "period": "DAILY",
    "trendData": [
      {
        "date": "2024-01-01",
        "totalBookings": 25,
        "completedBookings": 20,
        "cancelledBookings": 2,
        "pendingBookings": 3
      }
    ]
  }
}
```

## 2. Booking Management APIs

### List All Bookings
```http
GET /admin/bookings?page=0&size=20&sortBy=createdAt&sortDirection=desc&status=COMPLETED&customerName=John
```

**Query Parameters:**
- `status`: BookingStatus (PENDING, COMPLETED, CANCELLED, etc.)
- `customerId`: Long
- `partnerId`: Long
- `serviceCatalogId`: Long
- `variantId`: Long
- `customerName`: String
- `partnerName`: String
- `serviceName`: String
- `startDateFrom`: DateTime (ISO format)
- `startDateTo`: DateTime (ISO format)
- `createdFrom`: DateTime (ISO format)
- `createdTo`: DateTime (ISO format)
- `minPrice`: Double
- `maxPrice`: Double
- `district`: String
- `isCustomerAccepted`: Boolean
- `page`: Integer (default: 0)
- `size`: Integer (default: 20)
- `sortBy`: String (default: "createdAt")
- `sortDirection`: String (default: "desc")

### Get Booking Details
```http
GET /admin/bookings/{id}
```

### Update Booking Status
```http
PUT /admin/bookings/{id}/status
Content-Type: application/json

{
  "status": "COMPLETED",
  "reason": "Admin completed booking"
}
```

### Admin Cancel Booking
```http
PUT /admin/bookings/{id}/cancel
Content-Type: application/json

{
  "reason": "Admin cancelled due to policy violation"
}
```

### Admin Refund Booking
```http
POST /admin/bookings/{id}/refund
Content-Type: application/json

{
  "refundAmount": 150000.0,
  "reason": "Service quality issue"
}
```

## 3. User Management APIs

### List All Users
```http
GET /admin/users?page=0&size=20&fullName=John&isActive=true&roleName=CUSTOMER
```

**Query Parameters:**
- `fullName`: String
- `phone`: String
- `email`: String
- `username`: String
- `isActive`: Boolean
- `gender`: Gender (MALE, FEMALE)
- `roleName`: String
- `createdFrom`: DateTime
- `createdTo`: DateTime
- `page`: Integer (default: 0)
- `size`: Integer (default: 20)
- `sortBy`: String (default: "createdAt")
- `sortDirection`: String (default: "desc")

### Get User Details
```http
GET /admin/users/{id}
```

### Update User Information
```http
PUT /admin/users/{id}
Content-Type: application/json

{
  "fullName": "John Doe Updated",
  "email": "john.updated@example.com",
  "address": "123 New Street",
  "gender": "MALE",
  "avatarUrl": "https://example.com/avatar.jpg",
  "isActive": true
}
```

### Update User Status
```http
PUT /admin/users/{id}/status?isActive=false
```

### Update User Role
```http
PUT /admin/users/{id}/role
Content-Type: application/json

{
  "roleId": 2
}
```

## 4. Partner Management APIs

### List All Partners
```http
GET /admin/partners?page=0&size=20&partnerName=Jane&isAvailable=true&district=District 1
```

**Query Parameters:**
- `partnerName`: String
- `phone`: String
- `email`: String
- `isAvailable`: Boolean
- `isActive`: Boolean
- `district`: String
- `createdFrom`: DateTime
- `createdTo`: DateTime
- `page`: Integer (default: 0)
- `size`: Integer (default: 20)
- `sortBy`: String (default: "createdAt")
- `sortDirection`: String (default: "desc")

### Get Partner Details
```http
GET /admin/partners/{id}
```

### Update Partner Status
```http
PUT /admin/partners/{id}/status
Content-Type: application/json

{
  "isAvailable": false
}
```

### Update Partner Districts
```http
PUT /admin/partners/{id}/districts
Content-Type: application/json

{
  "districtIds": [1, 2, 3]
}
```

### Get Partner's Bookings
```http
GET /admin/partners/{id}/bookings?page=0&size=20
```

## 5. Service Catalog Management APIs

### List All Service Catalogs
```http
GET /admin/service-catalogs?page=0&size=20&name=Cleaning&isActive=true
```

**Query Parameters:**
- `name`: String
- `isActive`: Boolean
- `minPlatformFeePercent`: Double
- `maxPlatformFeePercent`: Double
- `createdFrom`: DateTime
- `createdTo`: DateTime
- `page`: Integer (default: 0)
- `size`: Integer (default: 20)
- `sortBy`: String (default: "createdAt")
- `sortDirection`: String (default: "desc")

### Get Service Catalog Details
```http
GET /admin/service-catalogs/{id}
```

### Create Service Catalog
```http
POST /admin/service-catalogs
Content-Type: application/json

{
  "name": "House Cleaning",
  "description": "Professional house cleaning services",
  "imageUrl": "https://example.com/cleaning.jpg",
  "platformFeePercent": 20.0,
  "isActive": true
}
```

### Update Service Catalog
```http
PUT /admin/service-catalogs/{id}
Content-Type: application/json

{
  "name": "Premium House Cleaning",
  "description": "Premium professional house cleaning services",
  "platformFeePercent": 25.0,
  "isActive": true
}
```

### Delete Service Catalog
```http
DELETE /admin/service-catalogs/{id}
```

## Error Handling

### Common Error Codes

- `400` - Bad Request (validation errors)
- `404` - Not Found (resource doesn't exist)
- `500` - Internal Server Error

### Error Response Format
```json
{
  "code": 404,
  "message": "User not found"
}
```

### Validation Error Response
```json
{
  "code": 400,
  "message": "Validation failed",
  "errors": [
    {
      "field": "email",
      "message": "Email is required"
    }
  ]
}
```

## Frontend Integration Examples

### React/JavaScript Example

```javascript
// API Service Class
class AdminApiService {
  constructor(baseUrl = 'http://localhost:8080/api/v1') {
    this.baseUrl = baseUrl;
  }

  async getDashboardStats() {
    const response = await fetch(`${this.baseUrl}/admin/dashboard/stats`);
    return response.json();
  }

  async getBookings(filters = {}) {
    const params = new URLSearchParams(filters);
    const response = await fetch(`${this.baseUrl}/admin/bookings?${params}`);
    return response.json();
  }

  async updateBookingStatus(bookingId, statusData) {
    const response = await fetch(`${this.baseUrl}/admin/bookings/${bookingId}/status`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(statusData),
    });
    return response.json();
  }

  async getUsers(filters = {}) {
    const params = new URLSearchParams(filters);
    const response = await fetch(`${this.baseUrl}/admin/users?${params}`);
    return response.json();
  }
}

// Usage Example
const adminApi = new AdminApiService();

// Get dashboard stats
adminApi.getDashboardStats().then(data => {
  console.log('Dashboard stats:', data.result);
});

// Get bookings with filters
adminApi.getBookings({
  status: 'COMPLETED',
  page: 0,
  size: 20,
  sortBy: 'createdAt',
  sortDirection: 'desc'
}).then(data => {
  console.log('Bookings:', data.result.content);
});
```

### Vue.js Example

```javascript
// Composable for Admin API
import { ref, reactive } from 'vue'

export function useAdminApi() {
  const baseUrl = 'http://localhost:8080/api/v1'
  const loading = ref(false)
  const error = ref(null)

  const dashboardStats = reactive({
    totalUsers: 0,
    totalBookings: 0,
    totalRevenue: 0
  })

  const fetchDashboardStats = async () => {
    loading.value = true
    try {
      const response = await fetch(`${baseUrl}/admin/dashboard/stats`)
      const data = await response.json()
      Object.assign(dashboardStats, data.result)
    } catch (err) {
      error.value = err.message
    } finally {
      loading.value = false
    }
  }

  const updateBookingStatus = async (bookingId, statusData) => {
    loading.value = true
    try {
      const response = await fetch(`${baseUrl}/admin/bookings/${bookingId}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(statusData),
      })
      return await response.json()
    } catch (err) {
      error.value = err.message
      throw err
    } finally {
      loading.value = false
    }
  }

  return {
    loading,
    error,
    dashboardStats,
    fetchDashboardStats,
    updateBookingStatus
  }
}
```

## Best Practices

### 1. Pagination
- Always use pagination for list endpoints
- Default page size is 20, maximum recommended is 100
- Use `page` (0-based) and `size` parameters

### 2. Filtering
- Combine multiple filters for precise results
- Use date ranges for time-based filtering
- Text filters support partial matching (case-insensitive)

### 3. Sorting
- Default sorting is by `createdAt` in descending order
- Available sort directions: `asc`, `desc`
- Common sortable fields: `createdAt`, `updatedAt`, `name`, `totalPrice`

### 4. Error Handling
- Always check the `code` field in responses
- Handle network errors gracefully
- Implement retry logic for failed requests

### 5. Performance
- Use appropriate page sizes
- Implement client-side caching for static data
- Debounce search inputs to reduce API calls

## Testing with Postman

A Postman collection is available with all endpoints pre-configured:

1. Import the collection: `FTask_Admin_API.postman_collection.json`
2. Set environment variable `baseUrl` to `http://localhost:8080/api/v1`
3. All requests are ready to use with example data

## Support

For technical support or questions about the API integration:

- Check the error response messages for specific guidance
- Verify request format matches the examples provided
- Ensure all required fields are included in requests
- Contact the backend development team for additional assistance

---

**Last Updated**: November 2024  
**API Version**: v1.0  
**Documentation Version**: 1.0
