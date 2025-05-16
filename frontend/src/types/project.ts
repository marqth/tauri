import { z } from "zod"

export const ProjectPhaseSchema = z.enum([
	"COMPOSING",
	"PREPUBLISHED",
	"PUBLISHED",
	"FINISHED"
])
export type ProjectPhase = z.infer<typeof ProjectPhaseSchema>

export const ProjectSchema = z.object({
	id: z.number(),
	name: z.string(),
	nbTeams: z.coerce.number().nullable(),
	nbWomen: z.coerce.number().nullable(),
	phase: ProjectPhaseSchema,
	actual: z.boolean()
})
export type Project = z.infer<typeof ProjectSchema>

export const CreateProjectSchema = ProjectSchema.omit({
	id: true
})
export type CreateProject = z.infer<typeof CreateProjectSchema>

export const UpdateProjectSchema = CreateProjectSchema.partial()
export type UpdateProject = z.infer<typeof UpdateProjectSchema>