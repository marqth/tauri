package fr.eseo.tauri.model.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum RoleType {

    SUPERVISING_STAFF,
    OPTION_LEADER,
    PROJECT_LEADER,
    OPTION_STUDENT,
    TEAM_MEMBER,
    TECHNICAL_COACH,
    SYSTEM_ADMINISTRATOR,
    JURY_MEMBER,
    ESEO_ADMINISTRATION,
    IDENTIFIED_USER;

}
