package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.*;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.*;
import fr.eseo.tauri.service.PermissionService;
import fr.eseo.tauri.service.RoleService;
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
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserService userService;

    @Mock
    private PermissionService permissionService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private GradeTypeRepository gradeTypeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private GradeRepository gradeRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRoleByIdShouldReturnRoleWhenAuthorizedAndIdExists() {
        Integer id = 1;
        Role role = new Role();
        role.id(id);

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        Role result = roleService.getRoleById(id);

        assertEquals(role, result);
    }

    @Test
    void getRoleByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.getRoleById(id));
    }

    @Test
    void createRoleShouldSaveRoleWhenAuthorizedAndUserIdExists() {
        Role role = new Role();
        role.userId(1);
        User user = new User();
        user.id(1);

        when(userService.getUserById(role.userId())).thenReturn(user);

        roleService.createRole(role);

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void createRoleShouldSaveRoleWhenAuthorizedAndUserIdIsNull() {
        Role role = new Role();

        roleService.createRole(role);

        verify(roleRepository, times(1)).save(role);
    }

    @Test
    void deleteRoleByIdShouldDeleteRoleWhenAuthorizedAndIdExists() {
        Integer id = 1;
        Role role = new Role();
        role.id(id);

        when(roleRepository.findById(id)).thenReturn(Optional.of(role));

        roleService.deleteRoleById(id);

        verify(roleRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteRoleByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Integer id = 1;

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRoleById(id));
    }

    @Test
    void deleteAllRolesShouldDeleteAllRolesWhenAuthorized() {
        roleService.deleteAllRoles();

        verify(roleRepository, times(1)).deleteAll();
    }

    @Test
    void hasPermissionShouldReturnFalseWhenPermissionDoesNotExist() {
        RoleType roleType = RoleType.OPTION_LEADER;
        PermissionType permissionType = PermissionType.ADD_GRADE_COMMENT;
        List<Permission> permissions = List.of(new Permission());

        when(permissionService.getAllPermissionsByRole(roleType)).thenReturn(permissions);

        Boolean result = roleService.hasPermission(roleType, permissionType);

        assertFalse(result);
    }


    @Test
    void createRolesShouldCreateRoleWhenRoleTypeIsNotOptionStudent() {
        String email = "test@test.com";
        RoleType[] roles = {RoleType.OPTION_LEADER};
        Integer projectId = 1;

        User user = new User();
        user.email(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        roleService.createRoles(email, roles, projectId);

        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void getAllRolesShouldReturnAllRolesWhenRolesExist() {
        Role role1 = new Role();
        Role role2 = new Role();
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertEquals(roles, result);
    }

    @Test
    void getAllRolesShouldReturnEmptyListWhenNoRolesExist() {
        List<Role> roles = Collections.emptyList();

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getAllRoles();

        assertTrue(result.isEmpty());
    }

    @Test
    void createRolesShouldCreateStudentRoleAndDeleteUserWhenRoleTypeIsOptionStudent() {
        String email = "test@test.com";
        RoleType[] roles = {RoleType.OPTION_STUDENT};
        Integer projectId = 1;

        User user = new User();
        user.email(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(gradeTypeRepository.findByNameAndProjectId("MOYENNE", projectId)).thenReturn(new GradeType());
        when(gradeRepository.save(any(Grade.class))).thenReturn(new Grade());
        when(studentRepository.save(any(Student.class))).thenReturn(new Student());

        roleService.createRoles(email, roles, projectId);

        verify(userRepository, times(1)).delete(user);
        verify(roleRepository, times(1)).save(any(Role.class));
        verify(studentRepository, times(1)).save(any(Student.class));
        verify(gradeRepository, times(1)).save(any(Grade.class));
    }

    @Test
    void emailToNameShouldReturnEmptyStringWhenEmailIsEmpty() {
        String email = "";

        String result = roleService.emailToName(email);

        assertEquals("", result);
    }

    @Test
    void emailToNameShouldReturnCorrectFormatWhenEmailIsValid() {
        String email = "john.doe@test.com";

        String result = roleService.emailToName(email);

        assertEquals("DOE John", result);
    }

    @Test
    void updateRoleShouldUpdateRoleTypeWhenRoleTypeIsProvided() {
        Integer id = 1;
        Role existingRole = new Role();
        Role updatedRole = new Role();
        updatedRole.type(RoleType.OPTION_LEADER);

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));

        roleService.updateRole(id, updatedRole);

        assertEquals(RoleType.OPTION_LEADER, existingRole.type());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void updateRoleShouldUpdateUserWhenUserIdIsProvided() {
        Integer id = 1;
        Role existingRole = new Role();
        Role updatedRole = new Role();
        updatedRole.userId(2);
        User user = new User();
        user.id(2);

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));
        when(userService.getUserById(updatedRole.userId())).thenReturn(user);

        roleService.updateRole(id, updatedRole);

        assertEquals(user, existingRole.user());
        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void updateRoleShouldNotUpdateRoleWhenUpdatedRoleHasNoChanges() {
        Integer id = 1;
        Role existingRole = new Role();
        Role updatedRole = new Role();

        when(roleRepository.findById(id)).thenReturn(Optional.of(existingRole));

        roleService.updateRole(id, updatedRole);

        verify(roleRepository, times(1)).save(existingRole);
    }

    @Test
    void updateRoleShouldThrowResourceNotFoundExceptionWhenRoleDoesNotExist() {
        Integer id = 1;
        Role updatedRole = new Role();

        when(roleRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.updateRole(id, updatedRole));
    }

    @Test
    void getUsersByRoleTypeShouldReturnUsersWhenRolesExist() {
        RoleType roleType = RoleType.OPTION_LEADER;
        Role role1 = new Role();
        role1.user(new User());
        Role role2 = new Role();
        role2.user(new User());
        List<Role> roles = Arrays.asList(role1, role2);

        when(roleRepository.findByType(roleType)).thenReturn(roles);

        List<User> result = roleService.getUsersByRoleType(roleType);

        assertEquals(roles.size(), result.size());
        assertTrue(result.contains(role1.user()));
        assertTrue(result.contains(role2.user()));
    }

    @Test
    void getUsersByRoleTypeShouldReturnEmptyListWhenNoRolesExist() {
        RoleType roleType = RoleType.OPTION_LEADER;
        List<Role> roles = Collections.emptyList();

        when(roleRepository.findByType(roleType)).thenReturn(roles);

        List<User> result = roleService.getUsersByRoleType(roleType);

        assertTrue(result.isEmpty());
    }

}
