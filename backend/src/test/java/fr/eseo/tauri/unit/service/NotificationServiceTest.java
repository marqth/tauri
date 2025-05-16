package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Notification;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.repository.NotificationRepository;
import fr.eseo.tauri.service.NotificationService;
import fr.eseo.tauri.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class NotificationServiceTest {
    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private UserService userService;

    @InjectMocks
    private NotificationService notificationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getNotificationByIdShouldReturnNotificationWhenAuthorizedAndIdExists() {
        Integer id = 1;
        Notification notification = new Notification();
        notification.id(id);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(notification));

        Notification result = notificationService.getNotificationById(id);

        assertEquals(notification, result);
    }

    @Test
    void getNotificationByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.getNotificationById(id));
    }

    @Test
    void getAllNotificationsShouldReturnNotificationsWhenAuthorized() {
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());

        when(notificationRepository.findAll()).thenReturn(notifications);

        List<Notification> result = notificationService.getAllNotifications();

        assertEquals(notifications, result);
    }

    @Test
    void getAllNotificationsShouldReturnEmptyListWhenNoNotifications() {
        when(notificationRepository.findAll()).thenReturn(Collections.emptyList());

        List<Notification> result = notificationService.getAllNotifications();

        assertTrue(result.isEmpty());
    }

    @Test
    void createNotificationShouldSaveNotificationWhenAuthorized() {
        Notification notification = new Notification();
        notification.userFromId(1);
        notification.userToId(2);

        when(userService.getUserById(notification.userFromId())).thenReturn(new User());
        when(userService.getUserById(notification.userToId())).thenReturn(new User());

        notificationService.createNotification(notification);

        verify(notificationRepository, times(1)).save(notification);
    }

    @Test
    void deleteAllNotificationsShouldDeleteAllNotificationsWhenAuthorized() {
        notificationService.deleteAllNotifications();

        verify(notificationRepository, times(1)).deleteAll();
    }
    @Test
    void getNotificationsByUserShouldReturnNotificationsWhenAuthorizedAndNotificationsExist() {
        int userId = 1;
        List<Notification> notifications = Arrays.asList(new Notification(), new Notification());

        when(notificationRepository.findByUser(userId)).thenReturn(notifications);

        List<Notification> result = notificationService.getNotificationsByUser(userId);

        assertEquals(notifications, result);
    }

    @Test
    void updateNotificationShouldNotUpdateFieldsWhenNotProvided() {
        Integer id = 1;
        Notification existingNotification = new Notification();
        existingNotification.id(id);
        Notification updatedNotification = new Notification();

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existingNotification));

        notificationService.updateNotification(id, updatedNotification);

        assertNull(existingNotification.message());
        assertFalse(existingNotification.checked());
        assertNull(existingNotification.userTo());
        assertNull(existingNotification.userFrom());
        verify(notificationRepository, times(1)).save(existingNotification);
    }

    @Test
    void updateNotificationShouldUpdateFieldsWhenProvided() {
        Integer id = 1;
        Notification existingNotification = new Notification();
        existingNotification.id(id);
        Notification updatedNotification = new Notification();
        updatedNotification.message("UpdatedMessage");
        updatedNotification.checked(true);
        updatedNotification.userToId(2);
        updatedNotification.userFromId(1);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existingNotification));
        when(userService.getUserById(updatedNotification.userToId())).thenReturn(new User());
        when(userService.getUserById(updatedNotification.userFromId())).thenReturn(new User());

        notificationService.updateNotification(id, updatedNotification);

        assertEquals(updatedNotification.message(), existingNotification.message());
        assertEquals(updatedNotification.checked(), existingNotification.checked());
        verify(notificationRepository, times(1)).save(existingNotification);
    }

    @Test
    void changeCheckedNotificationShouldToggleCheckedStateWhenNotificationExists() {
        Integer id = 1;
        Notification existingNotification = new Notification();
        existingNotification.id(id);
        existingNotification.checked(false);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existingNotification));

        notificationService.changeCheckedNotification(id);

        assertTrue(existingNotification.checked());
        verify(notificationRepository, times(1)).save(existingNotification);
    }

    @Test
    void changeCheckedNotificationShouldThrowResourceNotFoundExceptionWhenNotificationDoesNotExist() {
        Integer id = 1;

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.changeCheckedNotification(id));
    }

    @Test
    void deleteNotificationByIdShouldDeleteNotificationWhenIdExists() {
        Integer id = 1;
        Notification existingNotification = new Notification();
        existingNotification.id(id);

        when(notificationRepository.findById(id)).thenReturn(Optional.of(existingNotification));

        notificationService.deleteNotificationById(id);

        verify(notificationRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteNotificationByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(notificationRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> notificationService.deleteNotificationById(id));
    }
}
