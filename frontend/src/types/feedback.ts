import { z } from "zod"
import { TeamSchema } from "@/types/team"
import { SprintSchema } from "@/types/sprint"
import { UserSchema } from "@/types/user"
import { StudentSchema } from "@/types/student"

export const FeedbackSchema = z.object({
	id: z.number(),
	content: z.string(),
	feedback: z.boolean(),
	team: TeamSchema.nullable(),
	student: StudentSchema.nullable(),
	sprint: SprintSchema,
	author: UserSchema
})

export const CreateFeedbackSchema = FeedbackSchema.omit({
	id: true,
	author: true,
	team: true,
	student: true,
	sprint: true
}).extend({
	authorId: z.coerce.number(),
	teamId: z.coerce.number().nullable(),
	studentId: z.coerce.number().nullable(),
	sprintId: z.coerce.number()
})


export type Feedback = z.infer<typeof FeedbackSchema>
export type CreateFeedback = z.infer<typeof CreateFeedbackSchema>
export const UpdateFeedbackSchema = CreateFeedbackSchema.partial()
export type UpdateFeedback = z.infer<typeof UpdateFeedbackSchema>