package vn.iotstar.repository;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import vn.iotstar.entity.Notification;
public interface NotificationRepository extends JpaRepository<Notification, Long> 
{
	 Long  countByIsReadFalseAndStatusIn(List<String> statuses);
	 
	 List<Notification> findByStatusIn(List<String> statuses);
	 
	 List<Notification> findByUser_UserIdAndStatus(Long userId, String status);
	 
	 @Query("SELECT n FROM Notification n WHERE n.user.userId = :userId AND n.order.status = :orderStatus")
	 List<Notification> findDistinctOrdersByUser_UserIdAndOrderStatus(@Param("userId") Long userId, @Param("orderStatus") String orderStatus);
}
