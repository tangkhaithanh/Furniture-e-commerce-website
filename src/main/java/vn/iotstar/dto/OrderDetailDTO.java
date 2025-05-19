package vn.iotstar.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class OrderDetailDTO
{
	private Long orderId;
    private BigDecimal totalAmount;
    private String status;
    private LocalDateTime createdAt;
    private String paymentMethod;
    private String customerName;
    private List<OrderItemDTO> items;
}
