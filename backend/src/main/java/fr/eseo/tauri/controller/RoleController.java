package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Role;
import fr.eseo.tauri.model.User;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.service.RoleService;
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
@RequestMapping("/api/roles")
@Tag(name = "roles")
public class RoleController {

    private final RoleService roleService;
    private final ResponseMessage responseMessage = new ResponseMessage("role");

    @GetMapping("/{id}")
    public ResponseEntity<Role> getRoleById(@PathVariable Integer id) {
        Role role = roleService.getRoleById(id);
        return ResponseEntity.ok(role);
    }

    @GetMapping
    public ResponseEntity<List<Role>> getAllRoles() {
        List<Role> roles = roleService.getAllRoles();
        return ResponseEntity.ok(roles);
    }

    @PostMapping
    public ResponseEntity<String> createRole(@Validated(Create.class) @RequestBody Role role) {
        roleService.createRole(role);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PostMapping(path="/{email}")
    public ResponseEntity<String> createRoles(@PathVariable String email, @RequestParam("projectId") Integer projectId, @Validated(Create.class) @RequestBody RoleType[] roles) {
        roleService.createRoles(email, roles, projectId);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updateRole(@PathVariable Integer id, @Validated(Update.class) @RequestBody Role updatedRole) {
        roleService.updateRole(id, updatedRole);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRoleById(@PathVariable Integer id) {
        roleService.deleteRoleById(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @GetMapping("/{roleType}/users")
    public ResponseEntity<List<User>> getUsersByRole(@PathVariable RoleType roleType) {
        var users = roleService.getUsersByRoleType(roleType);
        CustomLogger.info("Retrieved users by role type: " + users);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{roleType}/permissions/{permissionType}")
    public ResponseEntity<Boolean> hasPermission(@PathVariable RoleType roleType, @PathVariable PermissionType permissionType) {
        var hasPermission = roleService.hasPermission(roleType, permissionType);
        return ResponseEntity.ok(hasPermission);
    }

}