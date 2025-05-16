package fr.eseo.tauri.service;

import fr.eseo.tauri.model.Notification;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

	private final NotificationRepository notificationRepository;
	private final UserService userService;

	/**
	 * Get a notification by its id
	 * @param id the id of the notification
	 * @return the notification
	 */
	public Notification getNotificationById(Integer id) {
		return notificationRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("notification", id));
	}

	/**
	 * Get all notifications
	 * @return the list of notifications
	 */
	public List<Notification> getAllNotifications() {
		return notificationRepository.findAll();
	}

	/**
	 * Get a notification by the user id
	 * @param userId the id of the user who receive the notifications (userTo)
	 * @return the notifications
	 */
	public List<Notification> getNotificationsByUser(Integer userId) {
		return notificationRepository.findByUser(userId);
	}

	/**
	 * Create a notification
	 * @param notification the notification to create
	 */
	public void createNotification(Notification notification) {
		notification.userFrom(userService.getUserById(notification.userFromId()));
		notification.userTo(userService.getUserById(notification.userToId()));

		notificationRepository.save(notification);
	}

	/**
	 * Update a notification by its id
	 * @param id the id of the notification
	 * @param updatedNotification the updated notification
	 */
	public void updateNotification(Integer id, Notification updatedNotification) {
		var notification = getNotificationById(id);

		if (updatedNotification.message() != null) notification.message(updatedNotification.message());
		if (updatedNotification.checked() != null) notification.checked(updatedNotification.checked());
		if (updatedNotification.userToId() != null) notification.userTo(userService.getUserById(updatedNotification.userToId()));
		if (updatedNotification.userFromId() != null) notification.userFrom(userService.getUserById(updatedNotification.userFromId()));

		notificationRepository.save(notification);
	}


	/**
	 * Change the checked state of a notification
	 * @param id the id of the notification
	 */
	public void changeCheckedNotification(Integer id){
		var notification = getNotificationById(id);
		notification.checked(!notification.checked());
		notificationRepository.save(notification);
	}

	/**
	 * Delete a notification by its id
	 * @param id the id of the notification
	 */
	public void deleteNotificationById(Integer id) {
		getNotificationById(id);
		notificationRepository.deleteById(id);
	}

	/**
	 * Delete all notifications
	 */
	public void deleteAllNotifications() {
		notificationRepository.deleteAll();
	}

}