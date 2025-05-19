package vn.iotstar.dto;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class RevenueDTO {
	private List<String> dates;
    private List<BigDecimal> revenues;
    private BigDecimal totalRevenue;
    private Long totalOrders;
    private BigDecimal avgOrderValue;
}
