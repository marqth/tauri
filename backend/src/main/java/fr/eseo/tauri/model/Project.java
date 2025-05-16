package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.eseo.tauri.model.enumeration.ProjectPhase;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Entity
@Table(name = "projects")
@Data
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @JsonProperty
    private String name = "Nouveau projet";

    @NotNull(groups = { Create.class }, message = "The nbTeams field is required")
    @JsonProperty
    private Integer nbTeams;

    @NotNull(groups = { Create.class }, message = "The nbWomen field is required")
    @JsonProperty
    private Integer nbWomen;

    @Enumerated(EnumType.STRING)
    @Column(name="phase")
    @JsonProperty
    private ProjectPhase phase = ProjectPhase.COMPOSING;

    @JsonProperty
    @Column(columnDefinition = "TINYINT(1)")
    private Boolean actual = false;

}