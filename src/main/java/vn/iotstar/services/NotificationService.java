package vn.iotstar.services;

import java.util.List;

import vn.iotstar.entity.Notification;

public interface NotificationService 
{
	// lưu thông báo:
	void save(Notification notification);
	
	// tìm notification theo ID:
	Notification findById(Long id);
	
	Long countNotificationsByStatusAndNotRead(List<String> statuses);
	
	List<Notification> getNotificationsByStatus(List<String> statuses);
	
}
