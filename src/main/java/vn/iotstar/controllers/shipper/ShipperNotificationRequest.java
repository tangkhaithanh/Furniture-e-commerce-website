package vn.iotstar.controllers.shipper;

public class ShipperNotificationRequest 
{
	private Long shipperId;
    private Long orderId;
	public Long getShipperId() {
		return shipperId;
	}
	public void setShipperId(Long shipperId) {
		this.shipperId = shipperId;
	}
	public Long getOrderId() {
		return orderId;
	}
	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}
    
}
