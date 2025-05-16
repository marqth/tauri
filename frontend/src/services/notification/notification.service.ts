import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { CreateNotificationSchema, NotificationSchema } from "@/types/notification"
import type { Notification, CreateNotification } from "@/types/notification"
import { z } from "zod"
import { getUsersByRole } from "@/services/user"
import { Cookies } from "@/utils/cookie"
import type { RoleType } from "@/types/role"
import type { User } from "@/types/user"
import { getTeamById } from "@/services/team"
import { getStudentsByTeamId } from "@/services/student"


export const getAllNotifications = async(): Promise<Notification[]> => {
	const response = await queryAndValidate({
		route: "notifications",
		responseSchema: z.array(NotificationSchema)
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getAllNotificationsFromUser = async(userId: number): Promise<Notification[]> => {
	const response = await queryAndValidate({
		route: `users/${userId}/notifications`,
		responseSchema: NotificationSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}


export const changeStateChecked = async(id: number): Promise<void> => {
	const response = await mutateAndValidate({
		route: `notifications/${id}/changeStateChecked`,
		method: "PATCH"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const addNotification = async(userToId: number, userFromId: number, message: string): Promise<void> => {
	const notification: CreateNotification = {
		message,
		checked: false,
		type: "CREATE_TEAMS",
		userToId: userToId,
		userFromId: userFromId
	}

	const response = await mutateAndValidate({
		method: "POST",
		route: "notifications",
		body: notification,
		bodySchema: CreateNotificationSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const sendManyNotifications = async(message: string): Promise<void> => {
	const teamMembers = await getUsersByRole("TEAM_MEMBER")
	const supervisors = await getUsersByRole("SUPERVISING_STAFF")

	const userFromId = Cookies.getUserId()

	await Promise.all([...teamMembers, ...supervisors].map(async(userTo) => {
		const notification: CreateNotification = {
			message: message,
			checked: false,
			type: "CREATE_TEAMS",
			userToId: userTo.id,
			userFromId: userFromId
		}

		const response = await mutateAndValidate({
			method: "POST",
			route: "notifications",
			body: notification,
			bodySchema: CreateNotificationSchema
		})

		if (response.status === "error") {
			throw new Error(response.error)
		}
	}))
}

export const sendNotificationsByRole = async(message: string, roles: RoleType[], type: string): Promise<void> => {
	const getUsersByRoles = async(roles: RoleType[]): Promise<User[]> => {
		const usersByRoles = await Promise.all(roles.map(role => getUsersByRole(role)))
		return usersByRoles.flat()
	}

	const users = await getUsersByRoles(roles)
	const userIds = users.map(user => user.id)

	await sendNotificationsByUsers(message, userIds, type)
}

export const sendNotificationsByUsers = async(message: string, users: number[], type: string): Promise<void> => {

	const userFromId = Cookies.getUserId()

	await Promise.all(users.map(async(userTo) => {
		const notification: CreateNotification = {
			message: message,
			checked: false,
			type: type,
			userToId: userTo,
			userFromId: userFromId
		}

		const response = await mutateAndValidate({
			method: "POST",
			route: "notifications",
			body: notification,
			bodySchema: CreateNotificationSchema
		})

		if (response.status === "error") {
			throw new Error(response.error)
		}
	}))
}

export const sendNotificationsByTeam = async(message: string, teamId: number, type: string, leader: boolean): Promise<void> => {

	const students = await  getStudentsByTeamId(teamId, false)
	const studentIds = students.map(student => student.id)
	const team = await getTeamById(teamId)
	if (team.leader != null && leader) {
		await sendNotificationsByUsers(message, [team.leader.id], type)
	}
	await sendNotificationsByUsers(message, studentIds, type)
}