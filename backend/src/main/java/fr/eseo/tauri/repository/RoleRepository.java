package fr.eseo.tauri.repository;

import fr.eseo.tauri.model.User;
import fr.eseo.tauri.model.enumeration.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Role;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    @Query("SELECT r.type FROM Role r WHERE r.user = :user")
    List<RoleType> findByUser(User user);

    @Query("SELECT r FROM Role r WHERE r.type = :roleType")
    List<Role> findByType(RoleType roleType);

    @Query("SELECT r FROM Role r WHERE r.user = :user AND r.type = :roleType")
    Role findFirstByType(RoleType roleType);

    @Query("SELECT r FROM Role r WHERE r.user = :user AND r.type = :roleType")
    Role findFirstByUserAndType(User user, RoleType roleType);

}
