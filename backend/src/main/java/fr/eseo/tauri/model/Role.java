package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.enumeration.RoleType;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "roles")
@Data
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @JsonProperty
    private RoleType type;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User user;

    @NotNull(groups = { Create.class }, message = "The userId field is required")
    @Transient
    @JsonDeserialize
    private Integer userId;

}