import { describe, it, expect, vi } from "vitest"
import { getAllUsers,
	getConnectedUser,
	getUsersByRole,
	updateUser,
	getUserById,
	getAllPermissions,
	hasPermission,
	getCurrentUser,
	getAllRoles,
	createUser,
	deleteUser } from "./user.service"
import * as apiUtils from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { fakeUser } from "@/factories/user.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import type { RoleType } from "@/types/role"
import type { UpdateUser, CreateUser } from "@/types/user"
import type { PermissionType } from "@/types/permission"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(Cookies, "getUserId").mockReturnValue(1)
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getAllUsers", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const data = [fakeUser(), fakeUser()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const users = await getAllUsers()

		// Assertions
		expect(users).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "users",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllUsers()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users: 500 Internal Server Error")
	})
})

describe("getConnectedUser", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const user = fakeUser()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(user))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getConnectedUser()

		// Assertions
		expect(result).toEqual(user)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: `users/${Cookies.getUserId()}`,
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getConnectedUser()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users/1: 500 Internal Server Error")
	})
})

describe("getUsersByRole", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const users = [fakeUser(), fakeUser()]
		const role: RoleType = "PROJECT_LEADER"

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(users))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getUsersByRole(role)

		// Assertions
		expect(result).toEqual(users)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: `roles/${role}/users`,
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const role: RoleType = "PROJECT_LEADER"

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getUsersByRole(role)

		// Assertions
		await expect(promise).rejects.toThrow(`Failed to fetch GET roles/${role}/users: 500 Internal Server Error`)
	})
})

describe("updateUser", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const user: UpdateUser = {
			name: "New Name",
			email: "new.email@example.com",
			password: "newPassword",
			privateKey: "newPrivateKey"
		}

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await updateUser("1", user)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "PUT",
			route: "users/1",
			body: user,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const user: UpdateUser = {
			name: "New Name",
			email: "new.email@example.com",
			password: "newPassword",
			privateKey: "newPrivateKey"
		}

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = updateUser("1", user)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch PUT users/1: 500 Internal Server Error")
	})
})

describe("getUserById", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const user = fakeUser()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(user))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getUserById(1)

		// Assertions
		expect(result).toEqual(user)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "users/1",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getUserById(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users/1: 500 Internal Server Error")
	})
})

describe("getAllPermissions", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const permissions: PermissionType[] = ["LDAP", "LDAP"]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(permissions))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getAllPermissions(1)

		// Assertions
		expect(result).toEqual(permissions)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "users/1/permissions",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllPermissions(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users/1/permissions: 500 Internal Server Error")
	})
})

describe("hasPermission", () => {
	it("should return true if the user has the permission", () => {
		const permission: PermissionType = "LDAP"

		// Mock Cookies.getPermissions to return an array containing the permission
		vi.spyOn(Cookies, "getPermissions").mockReturnValue([permission])

		// Call the function
		const result = hasPermission(permission)

		// Assertions
		expect(result).toBe(true)
	})

	it("should return false if the user does not have the permission", () => {
		const permission: PermissionType = "LDAP"

		// Mock Cookies.getPermissions to return an empty array
		vi.spyOn(Cookies, "getPermissions").mockReturnValue([])

		// Call the function
		const result = hasPermission(permission)

		// Assertions
		expect(result).toBe(false)
	})
})

describe("getCurrentUser", () => {
	it("should call getUserById with correct arguments", async() => {
		const user = fakeUser()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(user))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getCurrentUser()

		// Assertions
		expect(result).toEqual(user)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "users/1",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getCurrentUser()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users/1: 500 Internal Server Error")
	})
})

describe("getAllRoles", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const roles: RoleType[] = ["PROJECT_LEADER", "PROJECT_LEADER"]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(roles))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getAllRoles(1)

		// Assertions
		expect(result).toEqual(roles)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "users/1/roles",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllRoles(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users/1/roles: 500 Internal Server Error")
	})
})

describe("createUser", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const user: CreateUser = {
			name: "testuser",
			password: "testpassword",
			email: "test@test.com",
			privateKey: "USER"
		}

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await createUser(user)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: "users",
			body: user,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const user: CreateUser = {
			name: "testuser",
			password: "testpassword",
			email: "test@test.com",
			privateKey: "USER"
		}

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = createUser(user)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch POST users: 500 Internal Server Error")
	})
})

describe("deleteUser", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await deleteUser(1)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "DELETE",
			route: "users/delete/1"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = deleteUser(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch DELETE users/delete/1: 500 Internal Server Error")
	})
})