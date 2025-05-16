import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { FlagSchema, type CreateFlag, CreateFlagSchema } from "@/types/flag"
import type { Flag } from "@/types/flag"
import { z } from "zod"
import { getConnectedUser } from "@/services/user"
import { Cookies } from "@/utils/cookie"

export const createFlag = async(body: Omit<CreateFlag, "authorId" | "projectId">): Promise<void> => {
	const user = await getConnectedUser()
	const projectId = Cookies.getProjectId()

	const response = await mutateAndValidate({
		method: "POST",
		route: "flags",
		body: { ...body, authorId: user.id, projectId },
		bodySchema: CreateFlagSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const createValidationFlag = async(): Promise<void> => {
	await createFlag({ type: "VALIDATION" })
}

export const createReportingFlag = async(description: string, studentId1?: number, studentId2?: number): Promise<void> => {
	if (!studentId1 || !studentId2) {
		await createFlag({ description, type: "REPORTING" })
	} else {
		await createFlag({ description, type: "REPORTING", firstStudentId: studentId1, secondStudentId: studentId2 })
	}
}

export const userHasValidateTeams = async(authorId: number): Promise<boolean> => {
	const response = await queryAndValidate({
		route: `flags/author/${authorId}/type/VALIDATION`,
		responseSchema: z.array(FlagSchema)
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data.length > 0
}

export const getAllFlags = async(): Promise<Flag[]> => {
	const response = await queryAndValidate({
		route: "flags",
		params: { projectId: Cookies.getProjectId().toString() },
		responseSchema: z.array(FlagSchema)
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const updateFlag = async(id: number, body: Partial<Flag>): Promise<void> => {
	const response = await mutateAndValidate({
		method: "PATCH",
		route: `flags/${id}`,
		body,
		bodySchema: FlagSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const getFlagsByConcernedTeam = async(teamId: number): Promise<Flag[]> => {
	const response = await queryAndValidate({
		route: `flags/team/${teamId}`,
		responseSchema: z.array(FlagSchema)
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}