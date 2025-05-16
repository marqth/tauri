import { describe, it, expect, vi } from "vitest"
import { getAllRoles, createRole } from "./role.service"
import * as apiUtils from "@/utils/api"
import { fakeRole } from "@/factories/role.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import type { RoleType } from "@/types/role"
import { Cookies } from "@/utils/cookie"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getAllRoles", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const roles = [fakeRole(), fakeRole()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(roles))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getAllRoles()

		// Assertions
		expect(result).toEqual(roles)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "roles",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllRoles()

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("createRole", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const roles: RoleType[] = ["PROJECT_LEADER", "PROJECT_LEADER"]
		const email = "test@test.com"

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await createRole(email, roles)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: `roles/${email}`,
			body: roles,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const roles: RoleType[] = ["PROJECT_LEADER", "PROJECT_LEADER"]
		const email = "test@test.com"

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = createRole(email, roles)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})