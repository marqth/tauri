import { z } from "zod"

export const AuthResponseSchema = z.object({
	id: z.number(),
	accessToken: z.string(),
	idProject: z.number()
})

export type AuthResponse = z.infer<typeof AuthResponseSchema>