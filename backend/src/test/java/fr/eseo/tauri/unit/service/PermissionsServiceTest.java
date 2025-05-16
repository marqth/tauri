package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Permission;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.PermissionRepository;
import fr.eseo.tauri.service.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class PermissionsServiceTest {

    @Mock
    private PermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getPermissionByIdShouldReturnPermissionWhenAuthorizedAndIdExists() {
        Integer id = 1;
        Permission permission = new Permission();
        permission.id(id);

        when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));

        Permission result = permissionService.getPermissionById(id);

        assertEquals(permission, result);
    }

    @Test
    void getPermissionByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(permissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.getPermissionById(id));
    }

    @Test
    void getAllPermissionsShouldReturnAllPermissionsWhenAuthorized() {
        List<Permission> permissions = Arrays.asList(new Permission(), new Permission());

        when(permissionRepository.findAll()).thenReturn(permissions);

        List<Permission> result = permissionService.getAllPermissions();

        assertEquals(permissions, result);
    }

    @Test
    void getAllPermissionsByRoleShouldReturnPermissionsWhenAuthorizedAndRoleExists() {
        RoleType roleType = RoleType.SUPERVISING_STAFF;
        List<Permission> permissions = Arrays.asList(new Permission(), new Permission());

        when(permissionRepository.findByRole(roleType)).thenReturn(permissions);

        List<Permission> result = permissionService.getAllPermissionsByRole(roleType);

        assertEquals(permissions, result);
    }

    @Test
    void getAllPermissionsByRoleShouldReturnEmptyListWhenNoPermissionsForRole() {
        RoleType roleType = RoleType.SUPERVISING_STAFF;

        when(permissionRepository.findByRole(roleType)).thenReturn(new ArrayList<>());

        List<Permission> result = permissionService.getAllPermissionsByRole(roleType);

        assertTrue(result.isEmpty());
    }

    @Test
    void createPermissionShouldSavePermissionWhenAuthorized() {
        Permission permission = new Permission();

        permissionService.createPermission(permission);

        verify(permissionRepository, times(1)).save(permission);
    }

    @Test
    void deletePermissionShouldDeletePermissionWhenAuthorizedAndIdExists() {
        Integer id = 1;
        Permission permission = new Permission();
        permission.id(id);

        when(permissionRepository.findById(id)).thenReturn(Optional.of(permission));

        permissionService.deletePermission(id);

        verify(permissionRepository, times(1)).deleteById(id);
    }

    @Test
    void deletePermissionShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(permissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.deletePermission(id));
    }

    @Test
    void deleteAllPermissionsShouldDeleteAllPermissionsWhenAuthorized() {
        permissionService.deleteAllPermissions();

        verify(permissionRepository, times(1)).deleteAll();
    }

    @Test
    void updatePermissionShouldUpdateFieldsWhenProvided() {
        Integer id = 1;
        Permission existingPermission = new Permission();
        existingPermission.id(id);
        Permission updatedPermission = new Permission();
        updatedPermission.type(PermissionType.VIEW_TEAM_CHANGES);
        updatedPermission.role(RoleType.SUPERVISING_STAFF);

        when(permissionRepository.findById(id)).thenReturn(Optional.of(existingPermission));

        permissionService.updatePermission(id, updatedPermission);

        assertEquals(updatedPermission.type(), existingPermission.type());
        assertEquals(updatedPermission.role(), existingPermission.role());
        verify(permissionRepository, times(1)).save(existingPermission);
    }

    @Test
    void updatePermissionShouldNotUpdateFieldsWhenNotProvided() {
        Integer id = 1;
        Permission existingPermission = new Permission();
        existingPermission.id(id);
        Permission updatedPermission = new Permission();

        when(permissionRepository.findById(id)).thenReturn(Optional.of(existingPermission));

        permissionService.updatePermission(id, updatedPermission);

        assertNull(existingPermission.type());
        assertNull(existingPermission.role());
        verify(permissionRepository, times(1)).save(existingPermission);
    }

    @Test
    void updatePermissionShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;
        Permission updatedPermission = new Permission();

        when(permissionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.updatePermission(id, updatedPermission));
    }
}
