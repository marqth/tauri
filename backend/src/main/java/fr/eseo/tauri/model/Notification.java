package fr.eseo.tauri.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.eseo.tauri.model.enumeration.NotificationType;
import fr.eseo.tauri.util.valid.Create;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notifications")
@Data
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonProperty
    private Integer id;

    @NotNull(groups = { Create.class }, message = "The message field is required")
    @JsonProperty
    private String message;

    @JsonProperty
    private Boolean checked = false;

    @NotNull(groups = { Create.class }, message = "The type field is required")
    @Enumerated(EnumType.STRING)
    @Column(name="type", columnDefinition = "TEXT")
    @JsonProperty
    private NotificationType type;

    @ManyToOne
    @JoinColumn(name = "user_to")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User userTo;

    @ManyToOne
    @JoinColumn(name = "user_from")
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private User userFrom;

    @NotNull(groups = { Create.class }, message = "The userToId field is required")
    @Transient
    @JsonDeserialize
    private Integer userToId;

    @NotNull(groups = { Create.class }, message = "The userFromId field is required")
    @Transient
    @JsonDeserialize
    private Integer userFromId;

}