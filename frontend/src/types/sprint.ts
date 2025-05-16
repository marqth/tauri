import { ProjectSchema } from "@/types/project"
import { z } from "zod"

export const SprintEndTypeSchema = z.enum([
	"NORMAL_SPRINT",
	"UNGRADED_SPRINT",
	"FINAL_SPRINT"
])
export type SprintEndType = z.infer<typeof SprintEndTypeSchema>

export const SprintSchema = z.object({
	id: z.number(),
	startDate: z.coerce.date(),
	endDate: z.coerce.date(),
	endType: SprintEndTypeSchema,
	sprintOrder: z.coerce.number(),
	project: ProjectSchema
})
export type Sprint = z.infer<typeof SprintSchema>

export const CreateSprintSchema = SprintSchema.omit({
	id: true,
	project: true,
	startDate: true,
	endDate: true
}).extend({
	projectId: z.coerce.number(),
	startDate: z.string(),
	endDate: z.string()
})
export type CreateSprint = z.infer<typeof CreateSprintSchema>

export const UpdateSprintSchema = CreateSprintSchema.partial()
export type UpdateSprint = z.infer<typeof UpdateSprintSchema>

export const formatSprintEndType = (sprintEndType: SprintEndType): string => {
	switch (sprintEndType) {
	case "NORMAL_SPRINT":
		return "Sprint normal"
	case "UNGRADED_SPRINT":
		return "Sprint non noté"
	case "FINAL_SPRINT":
		return "Sprint final"
	}
}

export const getSprintEndTypeDescription = (sprintEndType: SprintEndType): string => {
	switch (sprintEndType) {
	case "NORMAL_SPRINT":
		return "Sprint planning, sprint, sprint review avec présentation et démonstration [soutenance], sprint retrospective"
	case "UNGRADED_SPRINT":
		return "Sprint planning, sprint, possibilité de sprint review avec le client [non noté], sprint retrospective"
	case "FINAL_SPRINT":
		return "Sprint planning, sprint, sprint review avec présentation et démonstration [soutenance], sprint retrospective, présentation commerciale"
	}
}

export const formatSprintEndTypeWithDescription = (sprintEndType: SprintEndType): string => {
	return `${formatSprintEndType(sprintEndType)} : ${getSprintEndTypeDescription(sprintEndType)}`
}