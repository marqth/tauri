package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.eseo.tauri.model.enumeration.PermissionType;
import fr.eseo.tauri.model.enumeration.RoleType;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "permissions")
@Data
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name="permission")
    @JsonProperty
    private PermissionType type;

    @Enumerated(EnumType.STRING)
    @Column(name="role")
    @JsonProperty
    private RoleType role;

}