package fr.eseo.tauri.unit.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Permission;
import fr.eseo.tauri.model.Team;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.RoleRepository;
import fr.eseo.tauri.repository.TeamRepository;
import fr.eseo.tauri.repository.UserRepository;
import fr.eseo.tauri.service.PermissionService;
import fr.eseo.tauri.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Nested
class UserServiceTest {

    @InjectMocks
    UserService userService;

    @Mock
    UserRepository userRepository;

    @Mock
    TeamRepository teamRepository;

    @Mock
    PermissionService permissionService;

    @Mock
    RoleRepository roleRepository;

    @BeforeEach
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserByIdReturnsUserWhenAuthorizedAndUserExists() {
        Integer id = 1;
        User expectedUser = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(expectedUser));

        User actualUser = userService.getUserById(id);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserByIdThrowsResourceNotFoundExceptionWhenUserDoesNotExist() {
        Integer id = 1;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(id));
    }

    @Test
    void getAllUsersReturnsAllUsersWhenAuthorized() {
        List<User> expectedUsers = Arrays.asList(new User(), new User());

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void getAllUsersReturnsEmptyListWhenNoUsers() {
        List<User> expectedUsers = new ArrayList<>();

        when(userRepository.findAll()).thenReturn(expectedUsers);

        List<User> actualUsers = userService.getAllUsers();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void createUserSavesUserWhenAuthorized() throws IllegalArgumentException {
        User user = new User();

        userService.createUser(user);

        verify(userRepository, times(1)).save(user);
    }


    @Test
    void deleteAllUsersDeletesAllUsersWhenAuthorized() {
        userService.deleteAllUsers();

        verify(userRepository, times(1)).deleteAll();
    }

    @Test
    void updateUserShouldUpdateUserFieldsWhenUserExists() {
        Integer id = 1;
        User existingUser = new User().name("Old Name").email("old@email.com").password("oldPassword").privateKey("oldKey");
        User updatedUser = new User().name("New Name").email("new@email.com").password("newPassword").privateKey("newKey");

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        userService.updateUser(id, updatedUser);

        verify(userRepository, times(1)).save(argThat(user -> user.name().equals(updatedUser.name()) &&
                user.email().equals(updatedUser.email()) &&
                user.password().equals(updatedUser.password()) &&
                user.privateKey().equals(updatedUser.privateKey())));
    }

    @Test
    void updateUserShouldNotUpdateUserFieldsWhenUpdatedUserFieldsAreNull() {
        Integer id = 1;
        User existingUser = new User().name("Old Name").email("old@email.com").password("oldPassword").privateKey("oldKey");
        User updatedUser = new User();

        when(userRepository.findById(id)).thenReturn(Optional.of(existingUser));

        userService.updateUser(id, updatedUser);

        verify(userRepository, times(1)).save(argThat(user -> user.name().equals(existingUser.name()) &&
                user.email().equals(existingUser.email()) &&
                user.password().equals(existingUser.password()) &&
                user.privateKey().equals(existingUser.privateKey())));
    }

    @Test
    void updateUserShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        Integer id = 1;
        User updatedUser = new User();

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.updateUser(id, updatedUser));
    }

    @Test
    void deleteUserByIdShouldDeleteUserAndUnassignTeamsWhenUserExistsAndIsLeader() {
        Integer id = 1;
        User user = new User().id(id);
        Team team1 = new Team().leader(user);
        Team team2 = new Team().leader(user);
        List<Team> teams = Arrays.asList(team1, team2);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(teamRepository.findAllByLeaderId(id)).thenReturn(teams);

        userService.deleteUserById(id);

        verify(teamRepository, times(2)).save(team1);
        verify(teamRepository, times(2)).save(team2);
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteUserByIdShouldDeleteUserAndNotUnassignTeamsWhenUserExistsAndIsNotLeader() {
        Integer id = 1;
        User user = new User().id(id);
        List<Team> teams = Collections.emptyList();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(teamRepository.findAllByLeaderId(id)).thenReturn(teams);

        userService.deleteUserById(id);

        verify(teamRepository, times(0)).save(any(Team.class));
        verify(userRepository, times(1)).deleteById(id);
    }

    @Test
    void deleteUserByIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        Integer id = 1;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.deleteUserById(id));
    }

    @Test
    void getRolesByUserIdShouldReturnRolesWhenUserExists() {
        Integer id = 1;
        User user = new User().id(id);
        List<RoleType> expectedRoles = Arrays.asList(RoleType.SUPERVISING_STAFF, RoleType.TEAM_MEMBER);

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(roleRepository.findByUser(user)).thenReturn(expectedRoles);

        List<RoleType> actualRoles = userService.getRolesByUserId(id);

        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    void getRolesByUserIdShouldReturnEmptyListWhenUserHasNoRoles() {
        Integer id = 1;
        User user = new User().id(id);
        List<RoleType> expectedRoles = Collections.emptyList();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(roleRepository.findByUser(user)).thenReturn(expectedRoles);

        List<RoleType> actualRoles = userService.getRolesByUserId(id);

        assertEquals(expectedRoles, actualRoles);
    }

    @Test
    void getRolesByUserIdShouldThrowResourceNotFoundExceptionWhenUserDoesNotExist() {
        Integer id = 1;

        when(userRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getRolesByUserId(id));
    }

    @Test
    void getTeamByMemberIdShouldReturnTeamWhenUserIsLeader() {
        Integer userId = 1;
        Integer projectId = 1;
        User user = new User().id(userId);
        Team expectedTeam = new Team().leader(user);
        List<RoleType> roles = Collections.singletonList(RoleType.SUPERVISING_STAFF);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getRolesByUserId(userId)).thenReturn(roles);
        when(teamRepository.findByLeaderId(userId, projectId)).thenReturn(expectedTeam);

        Team actualTeam = userService.getTeamByMemberId(userId, projectId);

        assertEquals(expectedTeam, actualTeam);
    }

    @Test
    void getTeamByMemberIdShouldReturnTeamWhenUserIsMember() {
        Integer userId = 1;
        Integer projectId = 1;
        User user = new User().id(userId);
        Team expectedTeam = new Team().leader(user);
        List<RoleType> roles = Collections.singletonList(RoleType.TEAM_MEMBER);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getRolesByUserId(userId)).thenReturn(roles);
        when(teamRepository.findByStudentId(userId)).thenReturn(expectedTeam);

        Team actualTeam = userService.getTeamByMemberId(userId, projectId);

        assertEquals(expectedTeam, actualTeam);
    }

    @Test
    void getTeamByMemberIdShouldReturnNullWhenUserIsNotMemberOrLeader() {
        Integer userId = 1;
        Integer projectId = 1;
        User user = new User().id(userId);
        List<RoleType> roles = Collections.emptyList();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userService.getRolesByUserId(userId)).thenReturn(roles);

        Team actualTeam = userService.getTeamByMemberId(userId, projectId);

        assertNull(actualTeam);
    }

    @Test
    void hasPermissionShouldReturnFalseWhenUserHasNoPermissions() {
        Integer id = 1;
        User user = new User().id(id);
        PermissionType permission = PermissionType.DELETE_PROJECT;
        List<PermissionType> permissions = Collections.emptyList();

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        when(userService.getPermissionsByUser(id)).thenReturn(permissions);

        Boolean result = userService.hasPermission(id, permission);

        assertFalse(result);
    }

    @Test
    void getUserByNameReturnsUserWhenUserExists() {
        String name = "John Doe";
        User expectedUser = new User();

        when(userRepository.findByName(name)).thenReturn(expectedUser);

        User actualUser = userService.getUserByName(name);

        assertEquals(expectedUser, actualUser);
    }

    @Test
    void getUserByNameReturnsNullWhenUserDoesNotExist() {
        String name = "Nonexistent User";

        when(userRepository.findByName(name)).thenReturn(null);

        User actualUser = userService.getUserByName(name);

        assertNull(actualUser);
    }

    @Test
    void testGetPermissionsByUser_UserFound_WithRoles_WithPermissions() {
        // Arrange
        Integer userId = 1;
        User user = new User().id(userId);
        RoleType role1 = RoleType.TEAM_MEMBER;
        RoleType role2 = RoleType.OPTION_LEADER;
        PermissionType permission1 = PermissionType.MANAGE_PROJECT;
        PermissionType permission2 = PermissionType.DELETE_PROJECT;
        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(roleRepository.findByUser(user)).thenReturn(List.of(role1, role2));
        when(permissionService.getAllPermissionsByRole(role1)).thenReturn(List.of(new Permission().type(permission1)));
        when(permissionService.getAllPermissionsByRole(role2)).thenReturn(List.of(new Permission().type(permission2)));

        // Act
        List<PermissionType> permissions = userService.getPermissionsByUser(userId);

        // Assert
        assertEquals(2, permissions.size());
        assertEquals(List.of(permission1, permission2), permissions);
    }

    @Test
    void testCreateUser_UserExists() {
        // Arrange
        String existingEmail = "existing@example.com";
        User user = new User().email(existingEmail);
        when(userRepository.findByEmail(existingEmail)).thenReturn(Optional.of(new User()));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @Test
    void testCreateUser_UserDoesNotExist() {
        // Arrange
        String newEmail = "new@example.com";
        User user = new User().email(newEmail);
        when(userRepository.findByEmail(newEmail)).thenReturn(Optional.empty());
        when(userRepository.save(user)).thenReturn(user);

        // Act
        User createdUser = userService.createUser(user);

        // Assert
        assertNotNull(createdUser);
        assertEquals(newEmail, createdUser.email());
    }
}

