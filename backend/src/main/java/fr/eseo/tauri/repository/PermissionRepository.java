package fr.eseo.tauri.repository;

import fr.eseo.tauri.model.enumeration.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import fr.eseo.tauri.model.Permission;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PermissionRepository extends JpaRepository<Permission, Integer> {

    @Query("SELECT p FROM Permission p WHERE p.role = :roleType")
    List<Permission> findByRole(RoleType roleType);

}
