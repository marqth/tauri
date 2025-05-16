import { describe, it, expect, vi } from "vitest"
import { getAllNotifications, getAllNotificationsFromUser, changeStateChecked, addNotification, sendManyNotifications } from "./notification.service"
import * as apiUtils from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { fakeNotification } from "@/factories/notification.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import type { CreateNotification } from "@/types/notification"
import type { User } from "@/types/user"
import { fakeUser } from "@/factories/user.factory"
import * as userService from "@/services/user"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(Cookies, "getUserId").mockReturnValue(1)
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getAllNotifications", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const notifications = [fakeNotification(), fakeNotification()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(notifications))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getAllNotifications()

		// Assertions
		expect(result).toEqual(notifications)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "notifications",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllNotifications()

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("getAllNotificationsFromUser", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const notifications = [fakeNotification(), fakeNotification()]
		const userId = 1

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(notifications))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getAllNotificationsFromUser(userId)

		// Assertions
		expect(result).toEqual(notifications)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: `users/${userId}/notifications`,
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const userId = 1

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllNotificationsFromUser(userId)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("changeStateChecked", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const id = 1

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await changeStateChecked(id)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "PATCH",
			route: `notifications/${id}/changeStateChecked`
		})
	})

	it("should throw an error if the response is an error", async() => {
		const id = 1

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = changeStateChecked(id)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("addNotification", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const notification: CreateNotification = {
			message: "Test message",
			checked: false,
			type: "CREATE_TEAMS",
			userToId: 2,
			userFromId: 1
		}

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await addNotification(notification.userToId, notification.userFromId, notification.message!)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: "notifications",
			body: notification,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const notification: CreateNotification = {
			message: "Test message",
			checked: false,
			type: "CREATE_TEAMS",
			userToId: 2,
			userFromId: 1
		}

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = addNotification(notification.userToId, notification.userFromId, notification.message!)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("sendManyNotifications", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const message = "Test message"
		const users: User[] = [fakeUser(), fakeUser()]

		// Mock getUsersByRole to return the users
		vi.spyOn(userService, "getUsersByRole").mockResolvedValue(users)

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValue(mockResponse)

		// Call the function
		await sendManyNotifications(message)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledTimes(4)
	})

	it("should throw an error if the response is an error", async() => {
		const message = "Test message"
		const users: User[] = [fakeUser(), fakeUser()]

		// Mock getUsersByRole to return the users
		vi.spyOn(userService, "getUsersByRole").mockResolvedValue(users)

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValue(mockResponse)

		// Call the function
		const promise = sendManyNotifications(message)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})