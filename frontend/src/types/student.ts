import { ProjectSchema } from "@/types/project"
import { TeamSchema } from "@/types/team"
import { UserSchema } from "@/types/user"
import { z } from "zod"

export const GenderSchema = z.enum([
	"MAN",
	"WOMAN"
]).nullable()
export type Gender = z.infer<typeof GenderSchema>

export const StudentSchema = UserSchema.extend({
	gender: GenderSchema,
	bachelor: z.boolean().nullable(),
	teamRole: z.string().nullable().optional(),
	team: TeamSchema.nullable(),
	project: ProjectSchema
})
export type Student = z.infer<typeof StudentSchema>

export const CreateStudentSchema = StudentSchema.omit({
	id: true,
	team: true,
	project: true
}).extend({
	gender: GenderSchema,
	bachelor: z.boolean(),
	name: z.string(),
	teamId: z.coerce.number().optional(),
	projectId: z.coerce.number()
})
export type CreateStudent = z.infer<typeof CreateStudentSchema>

export const UpdateStudentSchema = CreateStudentSchema.partial()
export type UpdateStudent = z.infer<typeof UpdateStudentSchema>