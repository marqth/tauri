import type { Sprint } from "@/types/sprint"
import { SprintSchema } from "@/types/sprint"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { z } from "zod"


export const getSprints = async(): Promise<Sprint[]> => {

	const response = await queryAndValidate({
		responseSchema: SprintSchema.array(),
		route: "sprints"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	if (response.data.length != 0) {
		response.data.sort((a, b) => a.sprintOrder - b.sprintOrder)
	}

	return response.data
}

export const getGradedSprints = async(): Promise<Sprint[]> => {
	const allSprints = await getSprints()
	return allSprints.filter(sprint => sprint.endType === "NORMAL_SPRINT" || sprint.endType === "FINAL_SPRINT")
}

export const addSprint = async(sprint: unknown): Promise<void> => {
	const response = await mutateAndValidate({
		method: "POST",
		body: sprint,
		route: "sprints",
		bodySchema: z.unknown()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const updateSprint = async(sprint: unknown, sprintId: number): Promise<void> => {
	const response = await mutateAndValidate({
		method: "PATCH",
		body: sprint,
		route: `sprints/${sprintId}`,
		bodySchema: z.unknown()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}


export const deleteSprint = async(sprintId: number | null): Promise<void> => {
	const response = await mutateAndValidate({
		method: "DELETE",
		route: `sprints/${sprintId}`
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}


export const getCurrentSprint = (sprints: Sprint[]) => {
	const currentDate = new Date()

	const sprint = sprints.find(sprint => {
		const startDate = new Date(sprint.startDate)
		startDate.setHours(0, 0, 0, 0) // Set time to 00:00:00

		const endDate = new Date(sprint.endDate)
		endDate.setHours(23, 59, 59, 999) // Set time to end of the day

		return startDate <= currentDate && currentDate <= endDate
	})

	return sprint || null
}