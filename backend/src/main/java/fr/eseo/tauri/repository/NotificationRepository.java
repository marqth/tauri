package fr.eseo.tauri.repository;

import fr.eseo.tauri.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    @Query("SELECT n FROM Notification n WHERE n.userTo.id= :user")
    List<Notification> findByUser(@Param("user") int id);

}
