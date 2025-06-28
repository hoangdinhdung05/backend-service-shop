package backend_service.shop.util;

public enum DiscountStatus {
    UPCOMING,   // Chưa bắt đầu (startDate > hiện tại)
    ACTIVE,     // Đang có hiệu lực (startDate <= now <= endDate) && isActive == true
    EXPIRED,    // Đã hết hạn (endDate < hiện tại)
    INACTIVE    // Bị admin tắt (isActive == false)
}