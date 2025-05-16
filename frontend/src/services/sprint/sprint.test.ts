import { describe, it, expect, vi } from "vitest"
import { getSprints,
	getGradedSprints,
	addSprint,
	updateSprint,
	deleteSprint } from "./sprint.service"
import * as apiUtils from "@/utils/api"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import { fakeSprint } from "@/factories/sprint.factory"
import { Cookies } from "@/utils/cookie"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getSprints", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const data = [fakeSprint(), fakeSprint()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const sprints = await getSprints()

		// Assertions
		expect(sprints).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "sprints",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getSprints()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET sprints: 500 Internal Server Error")
	})
})

describe("getGradedSprints", () => {
	it("should call getSprints and filter the result", async() => {
		const sprints = [fakeSprint(), fakeSprint()]
		sprints[0].endType = "NORMAL_SPRINT"
		sprints[1].endType = "NORMAL_SPRINT"

		// Mock getSprints to return the sprints
		const mockResponse = fakeResponse200(JSON.stringify(sprints))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const gradedSprints = await getGradedSprints()

		// Assertions
		expect(gradedSprints).toEqual(sprints)
	})

	it("should throw an error if getSprints throws an error", async() => {
		// Mock getSprints to throw an error
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getGradedSprints()

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("addSprint", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const sprint = { /* your sprint data */ }

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await addSprint(sprint)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: "sprints",
			body: sprint,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const sprint = { /* your sprint data */ }

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = addSprint(sprint)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("updateSprint", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const sprint = { /* your sprint data */ }
		const sprintId = 1

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await updateSprint(sprint, sprintId)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "PATCH",
			route: `sprints/${sprintId}`,
			body: sprint,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const sprint = { /* your sprint data */ }
		const sprintId = 1

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = updateSprint(sprint, sprintId)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("deleteSprint", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const sprintId = 1

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await deleteSprint(sprintId)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "DELETE",
			route: `sprints/${sprintId}`
		})
	})

	it("should throw an error if the response is an error", async() => {
		const sprintId = 1

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = deleteSprint(sprintId)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})