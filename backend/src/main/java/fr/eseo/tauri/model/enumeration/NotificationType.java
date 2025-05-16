package fr.eseo.tauri.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum NotificationType {

    BONUS_MALUS,
	CREATE_TEAMS,
    MOVE_STUDENT,
    DELETE_STUDENTS,
    CREATE_GRADE,
    IMPORT_STUDENTS;

}
