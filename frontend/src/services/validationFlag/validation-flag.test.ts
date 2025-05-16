import { describe, it, expect, vi } from "vitest"
import { getValidationFlagsByFlagId, updateValidationFlag } from "./validationFlag.service"
import * as apiUtils from "@/utils/api"
import { fakeValidationFlag } from "@/factories/validation-flag.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import { Cookies } from "@/utils/cookie"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getValidationFlagsByFlagId", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const data = [fakeValidationFlag(), fakeValidationFlag()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const flags = await getValidationFlagsByFlagId(1)

		// Assertions
		expect(flags).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "flags/1/validation",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getValidationFlagsByFlagId(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET flags/1/validation: 500 Internal Server Error")
	})
})

describe("updateValidationFlag", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const flagId = 1
		const authorId = 1
		const confirmed = true

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await updateValidationFlag(flagId, authorId, confirmed)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "PATCH",
			route: `flags/${flagId}/validation/${authorId}`,
			body: { flagId, authorId, confirmed },
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const flagId = 1
		const authorId = 1
		const confirmed = true

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = updateValidationFlag(flagId, authorId, confirmed)

		// Assertions
		await expect(promise).rejects.toThrow(`Failed to fetch PATCH flags/${flagId}/validation/${authorId}: 500 Internal Server Error`)
	})
})