package vn.iotstar.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.iotstar.entity.Notification;
import vn.iotstar.repository.NotificationRepository;
import vn.iotstar.services.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Override
    public void save(Notification notification) {
        // Lưu thông báo vào cơ sở dữ liệu
        notificationRepository.save(notification);
    }

	@Override
	public Notification findById(Long id) {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
	public Long countNotificationsByStatusAndNotRead(List<String> statuses) {
		return notificationRepository. countByIsReadFalseAndStatusIn(statuses);
	}

	@Override
	public List<Notification> getNotificationsByStatus(List<String> statuses) {
		return notificationRepository.findByStatusIn(statuses);
	}
}