import { z } from "zod"
import { UserSchema } from "@/types/user"
import { FlagSchema } from "@/types/flag"

export const ValidationFlagSchema = z.object({
	author: UserSchema,
	flag: FlagSchema,
	confirmed: z.boolean().nullable()
})

export const CreateValidationFlagSchema = ValidationFlagSchema.omit({
	author: true,
	flag: true
}).extend({
	authorId: z.number(),
	flagId: z.number()
})

export type ValidationFlag = z.infer<typeof ValidationFlagSchema>
export type CreateValidationFlag = z.infer<typeof CreateValidationFlagSchema>