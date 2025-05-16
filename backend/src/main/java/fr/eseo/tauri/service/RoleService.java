package fr.eseo.tauri.service;

import fr.eseo.tauri.model.*;
import fr.eseo.tauri.exception.ResourceNotFoundException;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.repository.*;
import fr.eseo.tauri.util.ListUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleService {

	private final RoleRepository roleRepository;
	private final UserService userService;
	private final PermissionService permissionService;
	private final UserRepository userRepository;
	private final StudentRepository studentRepository;
	private final GradeRepository gradeRepository;
	private final GradeTypeRepository gradeTypeRepository;

	public Role getRoleById(Integer id) {
		return roleRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("role", id));
	}

	public List<Role> getAllRoles() {
		return roleRepository.findAll();
	}

	public void createRole(Role role) {
		if(role.userId() != null) role.user(userService.getUserById(role.userId()));
		roleRepository.save(role);
	}

	public void createRoles(String email, RoleType[] roles, Integer projectId) {
		User user = userRepository.findByEmail(email).orElse(null);

		for (RoleType roleType : roles) {
			if(roleType == RoleType.OPTION_STUDENT) {
                userRepository.findByEmail(email).ifPresent(userRepository::delete);
                createStudentRoleAndGrades(email, projectId, roleType);
			} else {
				Role role = new Role();
				role.user(user);
				role.type(roleType);

				roleRepository.save(role);
			}
		}
	}

	public void createStudentRoleAndGrades(String email, Integer projectId, RoleType roleType){

		Student student = new Student();
		student.project(new Project().id(projectId));
		student.email(email);
		student.name(emailToName(email));
		studentRepository.save(student);

		Role role = new Role();
		role.user(student);
		role.type(roleType);
		roleRepository.save(role);

		GradeType gradeType = gradeTypeRepository.findByNameAndProjectId("MOYENNE", projectId);

		if (gradeType != null) {
			Grade grade = new Grade();
			grade.value(10f);
			grade.gradeType(gradeType);
			grade.student(student);
			grade.confirmed(false);
			gradeRepository.save(grade);
		}
	}

	public String emailToName(String email){
		String[] parts = email.split("@")[0].split("\\.");
		return (parts.length == 2)
				? parts[1].toUpperCase() + " " + parts[0].substring(0, 1).toUpperCase() + parts[0].substring(1).toLowerCase()
				: "";
	}

	public void updateRole(Integer id, Role updatedRole) {
		Role role = getRoleById(id);

		if (updatedRole.type() != null) role.type(updatedRole.type());
		if (updatedRole.userId() != null) role.user(userService.getUserById(updatedRole.userId()));

		roleRepository.save(role);
	}

	public void deleteRoleById(Integer id) {
		getRoleById(id);
		roleRepository.deleteById(id);
	}

	public void deleteAllRoles() {
		roleRepository.deleteAll();
	}

	/**
	 * Fetches all users associated with a specific role type.
	 *
	 * @param roleType The type of role to fetch users for.
	 * @return An iterable of users associated with the provided role type.
	 */
	public List<User> getUsersByRoleType(RoleType roleType) {
		var roles = roleRepository.findByType(roleType);
		return ListUtil.map(roles, Role::user);
	}

	public Boolean hasPermission(RoleType roleType, PermissionType permissionType) {
		var permissions = permissionService.getAllPermissionsByRole(roleType);
		return ListUtil.map(permissions, Permission::type).contains(permissionType);
	}

}