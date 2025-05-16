import { z } from "zod"

export const CriteriaSchema = z.object({
	nbWomens: z.coerce.number(),
	nbBachelors: z.coerce.number(),
	validCriteriaWoman: z.boolean(),
	validCriteriaBachelor: z.boolean()
})

export type Criteria = z.infer<typeof CriteriaSchema>;