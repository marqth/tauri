package fr.eseo.tauri.service;

import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.Team;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.RoleRepository;
import fr.eseo.tauri.repository.TeamRepository;
import fr.eseo.tauri.repository.UserRepository;
import fr.eseo.tauri.util.CustomLogger;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;
	private final TeamRepository teamRepository;
	private final RoleRepository roleRepository;
	private final PermissionService permissionService;



	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	public User getUserById(Integer id) {
		return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("user", id));
	}

	public User createUser(User user) throws IllegalArgumentException {
		User userCheck = userRepository.findByEmail(user.email()).orElse(null);
		if (userCheck != null) {
			throw new IllegalArgumentException("User already exists in the database");
		}
		return userRepository.save(user);
	}

	public void updateUser(Integer id, User updatedUser) {
		var user = getUserById(id);

		if (updatedUser.name() != null) user.name(updatedUser.name());
		if (updatedUser.email() != null) user.email(updatedUser.email());
		if (updatedUser.password() != null) user.password(updatedUser.password());
		if (updatedUser.privateKey() != null) user.privateKey(updatedUser.privateKey());

		userRepository.save(user);
	}

	public void deleteUserById(Integer id) {
		var user = getUserById(id);

		// Change team's leader to null when their leader is deleted.
		var teams = teamRepository.findAllByLeaderId(user.id());
		for (var team : teams) {
			team.leader(null);
			teamRepository.save(team);
		}

		userRepository.deleteById(id);
	}

	public void deleteAllUsers() {
		userRepository.deleteAll();
	}

	public List<RoleType> getRolesByUserId(Integer id) {
		User user = getUserById(id);
		return roleRepository.findByUser(user);
	}

	public Team getTeamByMemberId(Integer userId, Integer projectId) {
		List<RoleType> roles = getRolesByUserId(userId);
		CustomLogger.info("Roles of user " + userId + " : " + roles);

		if (roles.contains(RoleType.SUPERVISING_STAFF)) {
			return teamRepository.findByLeaderId(userId, projectId);
		} else if (roles.contains(RoleType.TEAM_MEMBER)) {
			return teamRepository.findByStudentId(userId);
		} else {
			CustomLogger.info(getUserById(userId).name() + "is not a member of any team");
			return null;
		}
	}

	public List<PermissionType> getPermissionsByUser(Integer id) {
		var user = getUserById(id);
		var roles = roleRepository.findByUser(user);

		List<PermissionType> permissions = new ArrayList<>();

		for (var role : roles) {
			var permissionsRoles = permissionService.getAllPermissionsByRole(role);
			for (var permission : permissionsRoles) {
				if (!permissions.contains(permission.type())) permissions.add(permission.type());
			}
		}

		return permissions;
	}

	public Boolean hasPermission(Integer id, PermissionType permission) {
		var permissions = getPermissionsByUser(id);

		return permissions.contains(permission);
	}

	public User getUserByName(String name){
		return userRepository.findByName(name);
	}

}
