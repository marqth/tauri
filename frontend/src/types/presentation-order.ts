import { SprintSchema } from "@/types/sprint"
import { StudentSchema } from "@/types/student"
import { z } from "zod"

export const PresentationOrderSchema = z.object({
	sprint: SprintSchema,
	student: StudentSchema,
	value: z.number()
})
export type PresentationOrder = z.infer<typeof PresentationOrderSchema>