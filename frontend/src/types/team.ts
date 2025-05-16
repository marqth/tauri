import { ProjectSchema } from "@/types/project"
import { UserSchema } from "@/types/user"
import { z } from "zod"

export const TeamSchema = z.object({
	id: z.number(),
	name: z.string(),
	project: ProjectSchema,
	leader: UserSchema.nullable()
})
export type Team = z.infer<typeof TeamSchema>

export const CreateTeamSchema = TeamSchema.omit({
	id: true,
	project: true,
	leader: true
}).extend({
	projectId: z.coerce.number(),
	leaderId: z.coerce.number().optional()
})
export type CreateTeam = z.infer<typeof CreateTeamSchema>

export const UpdateTeamSchema = CreateTeamSchema.partial()
export type UpdateTeam = z.infer<typeof UpdateTeamSchema>