import { type RoleType, RoleSchema } from "@/types/role"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { z } from "zod"
import { type Role } from "@/types/role"


export const getAllRoles = async(): Promise<Role[]> => {
	const response = await queryAndValidate({
		route: "roles",
		responseSchema: RoleSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const createRole = async(email: string, body: RoleType[]): Promise<void> => {

	const response = await mutateAndValidate({
		method: "POST",
		route: `roles/${email}`,
		body,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}