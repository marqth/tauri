package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.enumeration.FlagType;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Entity
@Table(name = "flags")
@Data
public class Flag implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @JsonProperty
    private String description;

    @NotNull(groups = { Create.class }, message = "The type field is required")
    @Enumerated(EnumType.STRING)
    @Column(name="type")
    @JsonProperty
    private FlagType type;

    @JsonProperty
    private Boolean status;

    @ManyToOne
    @JoinColumn(name = "first_student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Student firstStudent;

    @ManyToOne
    @JoinColumn(name = "second_student_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Student secondStudent;

    @ManyToOne
    @JoinColumn(name = "author_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User author;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Project project;

    @Transient
    @JsonDeserialize
    private Integer firstStudentId;

    @Transient
    @JsonDeserialize
    private Integer secondStudentId;

    @NotNull(groups = { Create.class }, message = "The authorId field is required")
    @Transient
    @JsonDeserialize
    private Integer authorId;

    @NotNull(groups = { Create.class }, message = "The projectId field is required")
    @Transient
    @JsonDeserialize
    private Integer projectId;

}
