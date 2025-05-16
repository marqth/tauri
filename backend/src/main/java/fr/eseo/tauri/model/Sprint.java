package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.enumeration.SprintEndType;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Table(name = "sprints", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"project_id", "sprint_order"}),
        @UniqueConstraint(columnNames = {"project_id", "start_date"}),
        @UniqueConstraint(columnNames = {"project_id", "end_date"})
})
@Data
public class Sprint implements Serializable {

    private static final long serialVersionUID = 2L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @NotNull(groups = { Create.class }, message = "The startDate field is required")
    @JsonProperty
    private LocalDate startDate;

    @NotNull(groups = { Create.class }, message = "The endDate field is required")
    @JsonProperty
    private LocalDate endDate;

    @NotNull(groups = { Create.class }, message = "The endType field is required")
    @Enumerated(EnumType.STRING)
    @Column(name="end_type")
    @JsonProperty
    private SprintEndType endType;

    @NotNull(groups = { Create.class }, message = "The sprintOrder field is required")
    @JsonProperty
    private Integer sprintOrder;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Project project;

    @NotNull(groups = { Create.class }, message = "The projectId field is required")
    @Transient
    @JsonDeserialize
    private Integer projectId;

}