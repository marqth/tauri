import {
	GradeDoubleArraySchema,
	GradeSchema,
	type Grade,
	CreateGradeSchema,
	type CreateGrade,
	GradeMapSchema,
	UpdateGradeSchema, type UpdateGrade,
	type IdentifyGrade
} from "@/types/grade"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { z } from "zod"
import { getConnectedUser } from "@/services/user"
import type { GradeTypeName } from "@/types/grade-type"
import { getGradeTypeByName } from "@/services/grade-type"
import { sendNotificationsByTeam, sendNotificationsByUsers } from "@/services/notification"


export const getAllRatedGradesFromConnectedUser = async(): Promise<Grade[]> => {
	const user = await getConnectedUser()

	const response = await queryAndValidate({
		route: `users/${user.id}/rated-grades`,
		responseSchema: GradeSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getRatedGrade = (allGrades: Grade[], body: IdentifyGrade): Grade | null => {
	const grade = allGrades.find(grade => grade.gradeType.name === body.gradeTypeName
			&& grade.sprint?.id === body.sprintId
			&& ((grade.student !== null && grade.student?.id === body.studentId) || (grade.team !== null && grade.team?.id === body.teamId)))

	return grade ?? null
}

export const createGrade = async(body: Omit<CreateGrade, "authorId" | "gradeTypeId"> & { gradeTypeName: GradeTypeName }): Promise<void> => {
	const user = await getConnectedUser()
	const gradeType = await getGradeTypeByName(body.gradeTypeName)

	const response	= await mutateAndValidate({
		method: "POST",
		route: "grades",
		body: {
			...body,
			authorId: user.id,
			gradeTypeId: gradeType.id
		},
		bodySchema: CreateGradeSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const updateGrade = async(id: number, body: UpdateGrade): Promise<void> => {
	const response = await mutateAndValidate({
		method: "PATCH",
		route: `grades/${id}`,
		body,
		bodySchema: UpdateGradeSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const createOrUpdateGrade = async(body: Omit<CreateGrade, "authorId" | "gradeTypeId"> & { gradeTypeName: GradeTypeName }): Promise<void> => {
	const allGrades = await getAllRatedGradesFromConnectedUser()
	const grade = await getRatedGrade(allGrades, body)

	if (grade) {
		await updateGrade(grade.id, body)
			.then(() => {
				if (body.gradeTypeName === "Performance individuelle") {
					void sendNotificationsByUsers(`La note de "${body.gradeTypeName}" du sprint ${body.sprintId} a été modifiée.`, [body.studentId], "CREATE_GRADE")
				} else {
					void sendNotificationsByTeam(`La note de "${body.gradeTypeName}" du sprint ${body.sprintId} a été modifiée.`, Number(body.teamId), "CREATE_GRADE", false)
				}
			})
	} else {
		await createGrade(body)
			.then(() => {
				if (body.gradeTypeName === "Performance individuelle") {
					void sendNotificationsByUsers(`La note de "${body.gradeTypeName}" du sprint ${body.sprintId} a été évaluée.`, [body.studentId], "CREATE_GRADE")
				} else {
					void sendNotificationsByTeam(`La note de "${body.gradeTypeName}" du sprint ${body.sprintId} a été évaluée.`, Number(body.teamId), "CREATE_GRADE", false)
				}
			})
	}
}


export const getAllImportedGrades = async(): Promise<Grade[]> => {
	const response = await queryAndValidate({
		route: "grades/imported",
		responseSchema: GradeSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getAllUnimportedGrades = async(): Promise<Grade[]> => {
	const response = await queryAndValidate({
		route: "grades/unimported",
		responseSchema: GradeSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data

}

export const getAverageGrades = async(userId: number): Promise<z.infer<typeof GradeDoubleArraySchema>> => {
	const response = await queryAndValidate({
		route: `grades/average-grades-by-grade-type-by-role/${userId}`,
		responseSchema: GradeDoubleArraySchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getAverageByGradeType = async(id: number, sprintId: number, gradeTypeName: string): Promise<number> => {
	const response = await queryAndValidate({
		route: `grades/average/${id}`,
		params: { sprintId: sprintId.toString(), gradeTypeName: gradeTypeName },
		responseSchema: z.number()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getTeamAverage = async(teamId: number, sprintId: string): Promise<z.infer<typeof GradeMapSchema>> => {
	const response = await queryAndValidate({
		route: `grades/average-team/${teamId}`,
		params: { sprintId: sprintId },
		responseSchema: GradeMapSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
	return response.data
}

export const getStudentsAverageByTeam = async(teamId: number, sprintId: string): Promise<z.infer<typeof GradeMapSchema>> => {
	const response = await queryAndValidate({
		route: `grades/average-students/${teamId}`,
		params: { sprintId: sprintId },
		responseSchema: GradeMapSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getTeamTotalGrade = async(teamId: number, sprintId: number): Promise<number> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/sprint/${sprintId}/total`,
		responseSchema: z.coerce.number()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getIndividualTotalGrade = async(studentId: number, sprintId: number): Promise<number> => {
	const response = await queryAndValidate({
		route: `students/${studentId}/sprint/${sprintId}/total`,
		responseSchema: z.coerce.number()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getSprintGrade = async(studentId: number, sprintId: number): Promise<number> => {
	const response = await queryAndValidate({
		route: `students/${studentId}/sprint/${sprintId}/grade`,
		responseSchema: z.number()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getIndividualTotalGrades = async(teamId: number, sprintId: number): Promise<number[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/sprint/${sprintId}/individual/totals`,
		responseSchema: z.coerce.number().array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getSprintGrades = async(teamId: number, sprintId: number): Promise<number[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/sprint/${sprintId}/grades`,
		responseSchema: z.number().array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getAverageSprintGrades = async(sprintId: number): Promise<number[]> => {
	const response = await queryAndValidate({
		route: `teams/sprint/${sprintId}/average`,
		responseSchema: z.number().array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}


export const getGradesConfirmation = async(sprintId: number, teamId: number): Promise<boolean> => {
	const response = await queryAndValidate({
		route: `grades/confirmation/${sprintId}/team/${teamId}`,
		// params: { ssTeam: ssTeam.toString() },
		responseSchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const setGradesConfirmation = async(teamId: number, sprintId: number) => {

	const response = await mutateAndValidate({
		method: "POST",
		route: `grades/confirmation/${sprintId}/team/${teamId}`,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const downloadGradesFile = async() => {
	const response = await queryAndValidate({
		route: "grades/download",
		responseSchema: z.string()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	const url = window.URL.createObjectURL(new Blob([response.data]))
	const link = document.createElement("a")
	link.href = url
	link.setAttribute("download", "grades.csv")
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
}

export const getGradeByGradeTypeAndAuthorAndSprint = async(studentId: number, gradeTypeId: number, authorId: number, sprintId: number): Promise<Grade | null> => {
	const response = await queryAndValidate({
		route: `students/${studentId}/gradeType/${gradeTypeId}/author/${authorId}`,
		params: { sprintId: sprintId.toString() },
		responseSchema: GradeSchema.nullable()
	})

	if (response.status === "error") {
		return null
	}

	return response.data
}


export const getIndividualGradesByTeam = async(sprintId: number, teamId: number): Promise<Grade[]> => {
	const response = await queryAndValidate({
		route: `grades/individual-grades-by-team/${sprintId}/${teamId}`,
		responseSchema: GradeSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}