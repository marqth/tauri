package fr.eseo.tauri.service;

import fr.eseo.tauri.model.Permission;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.PermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;

    public Permission getPermissionById(Integer id) {
        return permissionRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("permission", id));
    }

    public List<Permission> getAllPermissions() {
        return permissionRepository.findAll();
    }

    public List<Permission> getAllPermissionsByRole(RoleType roleType) {
        return permissionRepository.findByRole(roleType);
    }

    public void createPermission(Permission permission) {
        permissionRepository.save(permission);
    }

    public void updatePermission(Integer id, Permission updatedPermission) {
        Permission permission = getPermissionById(id);

        if (updatedPermission.type() != null) permission.type(updatedPermission.type());
        if (updatedPermission.role() != null) permission.role(updatedPermission.role());

        permissionRepository.save(permission);
    }

    public void deletePermission(Integer id) {
        getPermissionById(id);
        permissionRepository.deleteById(id);
    }

    public void deleteAllPermissions() {
        permissionRepository.deleteAll();
    }

}