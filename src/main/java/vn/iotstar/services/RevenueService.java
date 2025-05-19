package vn.iotstar.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import vn.iotstar.dto.RevenueDTO;

public interface RevenueService 
{
	RevenueDTO getRevenueData(LocalDate startDate, LocalDate endDate);
}
