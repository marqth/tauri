import { GradeTypeNameSchema, GradeTypeSchema } from "@/types/grade-type"
import { SprintSchema } from "@/types/sprint"
import { StudentSchema } from "@/types/student"
import { TeamSchema } from "@/types/team"
import { UserSchema } from "@/types/user"
import { z } from "zod"

export const GradeSchema = z.object({
	id: z.number(),
	value: z.coerce.number().nullable(),
	comment: z.string().nullable(),
	gradeType: GradeTypeSchema,
	author: UserSchema.nullable(),
	student: StudentSchema.nullable(),
	team: TeamSchema.nullable(),
	sprint: SprintSchema.nullable(),
	confirmed: z.boolean()
})
export type Grade = z.infer<typeof GradeSchema>

export const CreateGradeSchema = GradeSchema.omit({
	id: true,
	gradeType: true,
	author: true,
	student: true,
	team: true,
	sprint: true,
	confirmed: true
}).extend({
	gradeTypeId: z.coerce.number(),
	authorId: z.coerce.number(),
	studentId: z.coerce.number().nullable(),
	teamId: z.coerce.number().nullable(),
	sprintId: z.coerce.number().nullable()
})
export type CreateGrade = z.infer<typeof CreateGradeSchema>

export const UpdateGradeSchema = CreateGradeSchema.partial()
export type UpdateGrade = z.infer<typeof UpdateGradeSchema>

export const IdentifyGradeSchema = CreateGradeSchema.omit({
	value: true,
	comment: true,
	gradeTypeId: true,
	authorId: true
}).extend({
	gradeTypeName: GradeTypeNameSchema
})
export type IdentifyGrade = z.infer<typeof IdentifyGradeSchema>

export const GradeDoubleArraySchema = z.array(z.array(z.number().nullable()))

export const GradeMapSchema = z.record(z.number().nullable())