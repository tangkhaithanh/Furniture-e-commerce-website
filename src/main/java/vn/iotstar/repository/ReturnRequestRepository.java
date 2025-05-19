package vn.iotstar.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstar.entity.ReturnRequest;

@Repository
public interface ReturnRequestRepository extends JpaRepository<ReturnRequest, Long> 
{
		@Modifying
	    @Query("DELETE FROM ReturnRequest rr WHERE rr.order.id = :orderId")
	    void deleteByOrderId(@Param("orderId") Long orderId);
		
		Page<ReturnRequest> findAll(Pageable pageable);
}