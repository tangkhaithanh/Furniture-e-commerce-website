package vn.iotstar.services;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import vn.iotstar.entity.ReturnRequest;

public interface ReturnRequestService 
{
	List<ReturnRequest> getReturnRequests();
	
	Page<ReturnRequest> getReturnRequestsWithPagination(Pageable pageable);
}
