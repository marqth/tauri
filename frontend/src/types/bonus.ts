import { SprintSchema } from "@/types/sprint"
import { StudentSchema } from "@/types/student"
import { UserSchema } from "@/types/user"
import { z } from "zod"

export const BonusSchema = z.object({
	id: z.number(),
	value: z.coerce.number(),
	comment: z.string().nullable(),
	limited: z.boolean(),
	sprint: SprintSchema,
	student: StudentSchema,
	author: UserSchema.nullable()
})
export type Bonus = z.infer<typeof BonusSchema>

export const CreateBonusSchema = BonusSchema.omit({
	id: true,
	sprint: true,
	student: true,
	author: true
}).extend({
	sprintId: z.coerce.number(),
	studentId: z.coerce.number(),
	authorId: z.coerce.number().nullable()
})
export type CreateBonus = z.infer<typeof CreateBonusSchema>

export const UpdateBonusSchema = CreateBonusSchema.partial()
export type UpdateBonus = z.infer<typeof UpdateBonusSchema>