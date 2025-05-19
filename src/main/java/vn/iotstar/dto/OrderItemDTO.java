package vn.iotstar.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class OrderItemDTO 
{
	private String productName;
    private int quantity;
    private BigDecimal price;
    private String imageUrl;
}
