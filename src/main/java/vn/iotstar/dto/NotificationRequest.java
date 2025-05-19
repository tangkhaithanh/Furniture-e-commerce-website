package vn.iotstar.dto;

public class NotificationRequest 
{
	private Long orderId;  // Mã đơn hàng
    public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
	private Long shipperId;  // ID của shipper
	public Long getShipperId() {
		return shipperId;
	}
	public void setShipperId(Long shipperId) {
		this.shipperId = shipperId;
	}
}
