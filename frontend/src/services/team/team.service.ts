import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import type { Team, UpdateTeam } from "@/types/team"
import { TeamSchema } from "@/types/team"
import { z } from "zod"
import type { Criteria } from "@/types/criteria"
import { CriteriaSchema } from "@/types/criteria"
import { PresentationOrderSchema } from "@/types/presentation-order"
import { StudentSchema, type Student } from "@/types/student"


export const getTeams = async(): Promise<Team[]> => {
	const response = await queryAndValidate({
		route: "teams",
		responseSchema: TeamSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getTeamById = async(id: number): Promise<Team> => {
	const response = await queryAndValidate({
		route: `teams/${id}`,
		responseSchema: TeamSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const updateTeam = async(id: number, body: UpdateTeam): Promise<void> => {
	const response = await mutateAndValidate({
		route: `teams/${id}`,
		method: "PATCH",
		body,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const setTeamName = async(id: number, value: string): Promise<void> => {
	const response = await mutateAndValidate({
		route: `teams/update-name-team/${id}`,
		params: { newName: value },
		method: "PUT"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const setTeamLeader = async(id: number, value: string): Promise<void> => {
	const response = await mutateAndValidate({
		route: `teams/update-leader-team/${id}`,
		params: { idLeader: value },
		method: "PUT"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const generateTeams = async(nbTeams: string, nbWomen: string, autoWomenRatio: boolean): Promise<void> => {
	const response = await mutateAndValidate({
		method: "POST",
		route: "teams",
		params: { autoWomenRatio: autoWomenRatio.toString() },
		body: { nbTeams, nbWomen },
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const getCriteria = async(teamId: number): Promise<Criteria> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/criteria`,
		responseSchema: CriteriaSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
	return response.data
}

export const getTeamAverage = async(teamId: number): Promise<number> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/average`,
		responseSchema: z.number()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const deleteAllTeams = async(): Promise<void> => {
	const response = await mutateAndValidate({
		method: "DELETE",
		route: "teams"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const getTeamByUserId = async(userId: number): Promise<Team | null> => {
	const response = await queryAndValidate({
		route: `users/${userId}/team`,
		responseSchema: TeamSchema.or(z.literal(""))
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data === "" ? null : response.data
}

export const getTeamByLeaderId = async(leaderId: string | null, projectId: string | null): Promise<Team> => {
	const response = await queryAndValidate({
		route: `teams/leader/${leaderId}`,
		params: { projectId: projectId ?? "" },
		responseSchema: TeamSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getPresentationOrder = async(teamId: string, sprintId: string): Promise<Student[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/presentation-order`,
		params: { sprintId },
		responseSchema: PresentationOrderSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
		.sort((a, b) => a.value - b.value)
		.map(order => order.student)
}

export const updatePresentationOrder = async(teamId: string, sprintId: string, order: Student[]): Promise<void> => {
	const response = await mutateAndValidate({
		route: `teams/${teamId}/presentation-order`,
		params: { sprintId },
		method: "PATCH",
		body: order,
		bodySchema: StudentSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}