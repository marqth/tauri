package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Grade;
import fr.eseo.tauri.model.Notification;
import fr.eseo.tauri.model.Team;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.service.*;
import fr.eseo.tauri.util.CustomLogger;
import fr.eseo.tauri.util.ResponseMessage;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/api/users")
@Tag(name = "users")
public class UserController {

	private final UserService userService;
	private final ResponseMessage responseMessage = new ResponseMessage("user");
	private final RoleService roleService;
	private final GradeService gradeService;
	private final TeamService teamService;
	private final NotificationService notificationService;


	@GetMapping
	public List<User> getAllUsers() {
		return userService.getAllUsers();
	}

	@GetMapping("/{id}")
	public ResponseEntity<User> getUserById(@PathVariable Integer id) {
		User user = userService.getUserById(id);
		return ResponseEntity.ok(user);
	}

	@GetMapping(path = "/roles/{roleType}")
	public Iterable<User> getUsersByRole(@PathVariable RoleType roleType) {
		return roleService.getUsersByRoleType(roleType);
	}

	@PostMapping
	public ResponseEntity<User> createUser(@Validated(Create.class) @RequestBody User user) throws IllegalArgumentException {
		return ResponseEntity.ok(userService.createUser(user));
	}


	@PatchMapping("/{id}")
	public ResponseEntity<String> updateUser(@PathVariable Integer id, @Validated(Update.class) @RequestBody User user) {
		userService.updateUser(id, user);
		CustomLogger.info(responseMessage.update());
		return ResponseEntity.ok(responseMessage.update());
	}

	@DeleteMapping(path = "/delete/{id}")
	public ResponseEntity<String> deleteUserById(@PathVariable Integer id) {
		userService.deleteUserById(id);
		CustomLogger.info(responseMessage.delete());
		return ResponseEntity.ok(responseMessage.delete());
	}

	@DeleteMapping
	public ResponseEntity<String> deleteAllUsers() {
		userService.deleteAllUsers();
		CustomLogger.info(responseMessage.deleteAll());
		return ResponseEntity.ok(responseMessage.deleteAll());
	}

	@GetMapping(path = "/{id}/permissions/{permissionType}")
	public ResponseEntity<Boolean> hasPermission(@PathVariable Integer id, @PathVariable PermissionType permissionType) {
		var hasPermission = userService.hasPermission(id, permissionType);
		return ResponseEntity.ok(hasPermission);
	}

	@GetMapping(path = "/{id}/permissions")
	public ResponseEntity<List<PermissionType>> getAllPermissions(@PathVariable Integer id) {
		var permissions = userService.getPermissionsByUser(id);
		return ResponseEntity.ok(permissions);
	}

	@GetMapping("{id}/team")
	public ResponseEntity<Team> getTeamByMemberId(@PathVariable Integer id, @RequestParam("projectId") Integer projectId) {
		Team team = userService.getTeamByMemberId(id, projectId);
		CustomLogger.info("Team found: " + team);
		return ResponseEntity.ok(team);
	}

	@GetMapping("{id}/roles")
	public ResponseEntity<List<RoleType>> getRolesByUserId(@PathVariable Integer id) {
		List<RoleType> roles = userService.getRolesByUserId(id);
		return ResponseEntity.ok(roles);
	}

	@GetMapping("/{userId}/notifications")
	public ResponseEntity<List<Notification>> getAllNotificationsUser(@PathVariable Integer userId) {
		List<Notification> notifications = notificationService.getNotificationsByUser(userId);
		return ResponseEntity.ok(notifications);
	}

	@GetMapping("/{authorId}/rated-grades")
	public ResponseEntity<List<Grade>> getRatedGradesByAuthorId(@PathVariable Integer authorId) {
		var grades = gradeService.getRatedGradesByAuthorId(authorId);
		return ResponseEntity.ok(grades);
	}

	@GetMapping("/name/{name}")
	public ResponseEntity<User> getUserByName(@PathVariable String name){
		User user = userService.getUserByName(name);
		return ResponseEntity.ok(user);
	}

}