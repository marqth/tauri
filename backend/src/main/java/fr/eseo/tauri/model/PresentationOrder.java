package fr.eseo.tauri.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.eseo.tauri.model.id_class.PresentationOrderId;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "presentation_orders", uniqueConstraints = @UniqueConstraint(columnNames = {"sprint_id", "student_id"}))
@IdClass(PresentationOrderId.class)
@NoArgsConstructor
@RequiredArgsConstructor
@Data
public class PresentationOrder {

    @Id
    @ManyToOne
    @JoinColumn(name = "sprint_id")
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Sprint sprint;

    @Id
    @ManyToOne
    @JoinColumn(name = "student_id")
    @NonNull
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonProperty
    private Student student;

    @JsonProperty
    private Integer value;

}