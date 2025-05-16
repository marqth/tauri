package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.enumeration.Gender;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "students")
@OnDelete(action = OnDeleteAction.CASCADE)
@PrimaryKeyJoinColumn(name="user_id")
@Data
public class Student extends User {

    @NotNull(groups = { Create.class }, message = "The gender field is required")
    @Enumerated(EnumType.STRING)
    @Column(name="gender")
    @JsonProperty
    private Gender gender;

    @NotNull(groups = { Create.class }, message = "The bachelor field is required")
    @JsonProperty
    private Boolean bachelor;

    // Énumeration avec PO / SA / etc etc + laisser la possibilitié d'y rajouter des trucs à la main par les étudiants
    // Pour setup des trucs plus tard comme des canaux de discussion entre SA / PO et PL
    @JsonProperty
    private String teamRole = "Not assigned";

    @ManyToOne
    @JoinColumn(name = "team_id")
    @OnDelete(action = OnDeleteAction.NO_ACTION)
	@JsonProperty
    private Team team;

    @ManyToOne
    @JoinColumn(name = "project_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
	@JsonProperty
    private Project project;

    @Transient
    @JsonDeserialize
    private Integer teamId;

    @NotNull(groups = { Create.class }, message = "The projectId field is required")
    @Transient
    @JsonDeserialize
    private Integer projectId;

}