import { z } from "zod"
import { StudentSchema } from "./student"
import { UserSchema } from "./user"

export const FlagTypeSchema = z.enum([
	"REPORTING",
	"VALIDATION"
])
export type FlagType = z.infer<typeof FlagTypeSchema>

export const FlagSchema = z.object({
	id: z.number(),
	description: z.string().nullable(),
	type: FlagTypeSchema,
	firstStudent: StudentSchema.nullable(),
	secondStudent: StudentSchema.nullable(),
	author: UserSchema,
	status: z.boolean().nullable()
})
export type Flag = z.infer<typeof FlagSchema>

export const CreateFlagSchema = FlagSchema.omit({
	id: true,
	firstStudent: true,
	secondStudent: true,
	author: true,
	project: true,
	status: true
}).extend({
	firstStudentId: z.coerce.number().optional(),
	secondStudentId: z.coerce.number().optional(),
	authorId: z.coerce.number(),
	projectId: z.coerce.number(),
	description: z.string().optional()
})
export type CreateFlag = z.infer<typeof CreateFlagSchema>

export const UpdateFlagSchema = CreateFlagSchema.partial()
export type UpdateFlag = z.infer<typeof UpdateFlagSchema>