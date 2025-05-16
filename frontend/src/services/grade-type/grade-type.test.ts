import { describe, it, expect, vi } from "vitest"
import { getAllImportedGradeTypes,
	getAllUnimportedGradeTypes } from "./grade-type.service"
import * as apiUtils from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import { fakeGradeType } from "@/factories/grade-type.factory"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(Cookies, "getUserId").mockReturnValue(1)
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getAllImportedGradeTypes", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const data = [fakeGradeType(), fakeGradeType()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const gradeTypes = await getAllImportedGradeTypes()

		// Assertions
		expect(gradeTypes).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			responseSchema: expect.any(Object),
			route: "grade-types/imported"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllImportedGradeTypes()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET grade-types/imported: 500 Internal Server Error")
	})
})

describe("getAllUnimportedGradeTypes", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const data = [fakeGradeType(), fakeGradeType()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const gradeTypes = await getAllUnimportedGradeTypes()

		// Assertions
		expect(gradeTypes).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			responseSchema: expect.any(Object),
			route: "grade-types/unimported"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllUnimportedGradeTypes()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET grade-types/unimported: 500 Internal Server Error")
	})
})