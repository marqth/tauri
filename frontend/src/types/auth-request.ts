import { z } from "zod"

export const AuthRequestSchema = z.object({
	// eslint-disable-next-line camelcase
	login: z.string({ required_error: "L'adresse email est requise." })
		.email({ message: "L'adresse email n'est pas valide." }),
	// eslint-disable-next-line camelcase
	password: z.string({ required_error: "Le mot de passe est requis." })
})

export type AuthRequest = z.infer<typeof AuthRequestSchema>;