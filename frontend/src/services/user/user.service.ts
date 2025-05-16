import { PermissionTypeSchema, type PermissionType } from "@/types/permission"
import type { RoleType } from "@/types/role"
import { UserSchema, type CreateUser, type UpdateUser, type User } from "@/types/user"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { z } from "zod"
import { RoleTypeSchema } from "@/types/role"


export const getAllUsers = async(): Promise<User[]> => {
	const response = await queryAndValidate({
		route: "users",
		responseSchema: UserSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getConnectedUser = async(): Promise<User> => {
	const userId = Cookies.getUserId()

	const response = await queryAndValidate({
		route: `users/${userId}`,
		responseSchema: UserSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getUsersByRole = async(role: RoleType): Promise<User[]> => {
	const response = await queryAndValidate({
		route: `roles/${role}/users`,
		responseSchema: UserSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const updateUser = async(id: string | null, body: UpdateUser): Promise<void> => {
	const response = await mutateAndValidate({
		method: "PUT",
		route: `users/${id}`,
		body,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const getUserById = async(id: number): Promise<User> => {
	const response = await queryAndValidate({
		route: `users/${id}`,
		responseSchema: UserSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getUserByName = async(name: string) : Promise<User> => {
	const response = await queryAndValidate({
		route: `users/name/${name}`,
		responseSchema: UserSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getAllPermissions = async(id: number): Promise<PermissionType[]> => {
	const response = await queryAndValidate({
		route: `users/${id}/permissions`,
		responseSchema: PermissionTypeSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const hasPermission = (permission: PermissionType): boolean => {
	const permissions = Cookies.getPermissions()

	return permissions.includes(permission)
}

export const getCurrentUser = async(): Promise<User> => {
	const userId = Cookies.getUserId()

	return await getUserById(userId)
}

export const getAllRoles = async(id: number): Promise<RoleType[]> => {
	const response = await queryAndValidate({
		route: `users/${id}/roles`,
		responseSchema: RoleTypeSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const createUser = async(body: CreateUser): Promise<void> => {
	const response = await mutateAndValidate({
		method: "POST",
		route: "users",
		body,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}


export const deleteUser = async(id: number): Promise<void> => {
	const response = await mutateAndValidate({
		method: "DELETE",
		route: `users/delete/${id}`
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}