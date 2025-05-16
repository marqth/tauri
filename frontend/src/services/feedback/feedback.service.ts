import type { CreateFeedback, UpdateFeedback } from "@/types/feedback"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { CreateFeedbackSchema, FeedbackSchema, UpdateFeedbackSchema } from "@/types/feedback"
import { Cookies } from "@/utils/cookie"
import type { Feedback } from "@/types/feedback"

const currentUserId = Cookies.getUserId()

export const addComment = async(feedback: CreateFeedback): Promise<void> => {
	const response = await mutateAndValidate({
		method: "POST",
		route: "comments",
		body: feedback,
		bodySchema: CreateFeedbackSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const updateComment = async(id: number, feedback: UpdateFeedback): Promise<void> => {
	const response = await mutateAndValidate({
		method: "PATCH",
		route: `comments/${id}`,
		body: feedback,
		bodySchema: UpdateFeedbackSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const createComment = async(teamId: string | null, studentId: string | null, feedbackContent: string, sprintId: string, feedback: boolean): Promise<void> => {
	const authorId = currentUserId
	const comment: CreateFeedback = {
		teamId: teamId ? Number(teamId) : null,
		studentId: studentId ? Number(studentId) : null,
		content: feedbackContent,
		feedback,
		sprintId: Number(sprintId),
		authorId
	}
	return await addComment(comment)
}

export const getCommentsBySprintAndTeam = async(teamId: string, sprintId: string): Promise<Feedback[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/sprints/${sprintId}/feedbacks`,
		responseSchema: FeedbackSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getCommentsBySprintAndStudent = async(studentId: string, sprintId: string): Promise<Feedback[]> => {
	const response = await queryAndValidate({
		route: `students/${studentId}/sprints/${sprintId}/feedbacks`,
		responseSchema: FeedbackSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getTeamStudentsCommentsBySprintAndAuthor = async(sprintId: string, teamId: string): Promise<Feedback[]> => {
	const response = await queryAndValidate({
		route: `sprints/${sprintId}/author/${currentUserId}/student-comments`,
		params: { teamId: teamId },
		responseSchema: FeedbackSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getCommentsByTeamAndSprintAndAuthor = async(teamId: string, sprintId: string): Promise<Feedback[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/sprints/${sprintId}/author/${currentUserId}/feedbacks`,
		responseSchema: FeedbackSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getIndividualsCommentsBySprintIdAndTeamId = async(sprintId: string, teamId: string): Promise<Feedback[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/sprints/${sprintId}/individual-comments`,
		responseSchema: FeedbackSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}