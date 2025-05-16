package fr.eseo.tauri.controller;

import fr.eseo.tauri.model.Permission;
import fr.eseo.tauri.service.PermissionService;
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
@RequestMapping("/api/permissions")
@Tag(name = "permissions")
public class PermissionController {

    private final PermissionService permissionService;
    private final ResponseMessage responseMessage = new ResponseMessage("permission");

    @GetMapping("/{id}")
    public ResponseEntity<Permission> getPermissionById(@PathVariable Integer id) {
        Permission permission = permissionService.getPermissionById(id);
        return ResponseEntity.ok(permission);
    }

    @GetMapping
    public ResponseEntity<List<Permission>> getAllPermissionsByProject() {
        List<Permission> permissions = permissionService.getAllPermissions();
        return ResponseEntity.ok(permissions);
    }

    @PostMapping
    public ResponseEntity<String> createPermission(@Validated(Create.class) @RequestBody Permission permission) {
        permissionService.createPermission(permission);
        CustomLogger.info(responseMessage.create());
        return ResponseEntity.ok(responseMessage.create());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<String> updatePermission(@PathVariable Integer id, @Validated(Update.class) @RequestBody Permission updatedPermission) {
        permissionService.updatePermission(id, updatedPermission);
        CustomLogger.info(responseMessage.update());
        return ResponseEntity.ok(responseMessage.update());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePermission(@PathVariable Integer id) {
        permissionService.deletePermission(id);
        CustomLogger.info(responseMessage.delete());
        return ResponseEntity.ok(responseMessage.delete());
    }

    @DeleteMapping
    public ResponseEntity<String> deleteAllPermissionsByProject() {
        permissionService.deleteAllPermissions();
        CustomLogger.info(responseMessage.deleteAllFromCurrentProject());
        return ResponseEntity.ok(responseMessage.deleteAllFromCurrentProject());
    }
}