package vn.iotstar.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import vn.iotstar.entity.ReturnRequest;
import vn.iotstar.repository.ReturnRequestRepository;
import vn.iotstar.services.ReturnRequestService;
@Service
public class ReturnRequestImpl implements ReturnRequestService 
{

	@Autowired
	private ReturnRequestRepository returnRequestRepository;
	@Override
	public List<ReturnRequest> getReturnRequests()
	{
		return returnRequestRepository.findAll();
	}
	@Override
	public Page<ReturnRequest> getReturnRequestsWithPagination(Pageable pageable) {
		 return returnRequestRepository.findAll(pageable);
	}

}
