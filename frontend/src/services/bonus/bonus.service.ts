import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { type Bonus, BonusSchema, type CreateBonus, CreateBonusSchema, type UpdateBonus, UpdateBonusSchema } from "@/types/bonus"
import { getConnectedUser } from "@/services/user"
import { z } from "zod"

export const createBonus = async(body: Omit<CreateBonus, "authorId">): Promise<void> => {
	const user = await getConnectedUser()

	const response = await mutateAndValidate({
		method: "POST",
		route: "bonuses",
		body: { ...body, authorId: user.id },
		bodySchema: CreateBonusSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const updateBonus = async(id: number, body: UpdateBonus): Promise<void> => {

	const response = await mutateAndValidate({
		method: "PATCH",
		route: `bonuses/${id}`,
		body,
		bodySchema: UpdateBonusSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const getStudentBonus = async(studentId: number, limited: boolean, sprintId: string): Promise<Bonus> => {
	const response = await queryAndValidate({
		route: `students/${studentId}/bonus`,
		responseSchema: BonusSchema,
		params: { limited: limited.toString(), sprintId }
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getStudentBonuses = async(studentId: number, sprintId: string): Promise<Bonus[]> => {
	const response = await queryAndValidate({
		route: `students/${studentId}/bonuses`,
		responseSchema: z.array(BonusSchema),
		params: { sprintId }
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getValidationBonusesByTeam = async(teamId: number, sprintId: number): Promise<Bonus[]> => {
	const response = await queryAndValidate({
		route: `bonuses/teams/${teamId}`,
		responseSchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}



export const setValidationBonusesByTeam = async (teamId: number, sprintId: number, userId: number): Promise<void> => {

	const response = await mutateAndValidate({
		method: "PATCH",
		route: `bonuses/teams/${teamId}/${sprintId}/${userId}`,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}