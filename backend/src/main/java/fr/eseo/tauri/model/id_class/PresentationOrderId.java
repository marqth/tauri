package fr.eseo.tauri.model.id_class;

import com.fasterxml.jackson.annotation.JsonProperty;
import fr.eseo.tauri.model.Sprint;
import fr.eseo.tauri.model.Student;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
public class PresentationOrderId implements Serializable {

    @JsonProperty
    private Sprint sprint;

    @JsonProperty
    private Student student;

}
