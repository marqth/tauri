import { z } from "zod"
import { UserSchema } from "./user"

export const RoleTypeSchema = z.enum([
	"SUPERVISING_STAFF",
	"OPTION_LEADER",
	"PROJECT_LEADER",
	"OPTION_STUDENT",
	"TEAM_MEMBER",
	"SYSTEM_ADMINISTRATOR",
	"TECHNICAL_COACH"
])
export type RoleType = z.infer<typeof RoleTypeSchema>


export const RoleSchema = z.object({
	id: z.number(),
	type: RoleTypeSchema,
	user: UserSchema
})
export type Role = z.infer<typeof RoleSchema>


export const formatRole = (role: RoleType) => {
	switch (role) {
	case "SUPERVISING_STAFF":
		return "Professeur référent"
	case "OPTION_LEADER":
		return "Leader de l'option"
	case "PROJECT_LEADER":
		return "Leader du projet"
	case "OPTION_STUDENT":
		return "Étudiant"
	case "TEAM_MEMBER":
		return "Membre d'une équipe"
	case "SYSTEM_ADMINISTRATOR":
		return "Administrateur système"
	case "TECHNICAL_COACH":
		return "Coach technique"
	default:
		return role
	}
}