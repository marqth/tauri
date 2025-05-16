package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.util.valid.Create;
import fr.eseo.tauri.util.valid.Update;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "grade_types", uniqueConstraints = @UniqueConstraint(columnNames = { "name", "for_group", "imported", "project_id" }))
@Data
public class GradeType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @NotNull(groups = { Create.class }, message = "The name field is required")
    @JsonProperty
    private String name;

    @Min(value = 0, groups = { Create.class, Update.class }, message = "The factor field must be greater than or equal to 0")
    @JsonProperty
    private Float factor;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Project project;

    @NotNull(groups = { Create.class }, message = "The projectId field is required")
    @Transient
    @JsonDeserialize
    private Integer projectId;

    @NotNull(groups = { Create.class }, message = "The forGroup field is required")
    @JsonProperty
    private Boolean forGroup;

    @NotNull(groups = { Create.class }, message = "The imported field is required")
    @JsonProperty
    private Boolean imported;

    @Lob
    @Column(columnDefinition = "MEDIUMBLOB")
    @JsonProperty
    private byte[] scaleTXTBlob;

}