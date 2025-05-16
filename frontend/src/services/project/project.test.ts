import { describe, it, expect, vi } from "vitest"
import { getCurrentProject, getCurrentPhase, updateProject, createProject, setActualProject, getAllProjects, deleteProject } from "./project.service"
import * as apiUtils from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { fakeProject } from "@/factories/project.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import type { UpdateProject, CreateProject } from "@/types/project"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getCurrentProject", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const project = fakeProject()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(project))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getCurrentProject()

		// Assertions
		expect(result).toEqual(project)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: `projects/${Cookies.getProjectId()}`,
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getCurrentProject()

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("getCurrentPhase", () => {
	it("should return the current phase of the project", async() => {
		const project = fakeProject()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(project))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const phase = await getCurrentPhase()

		// Assertions
		expect(phase).toEqual(project.phase)
	})
})

describe("updateProject", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const update: UpdateProject = { /* your update data */ }

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await updateProject(update)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "PATCH",
			route: `projects/${Cookies.getProjectId()}`,
			body: update,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const update: UpdateProject = { /* your update data */ }

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = updateProject(update)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("createProject", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const create: CreateProject = {
			name: "New Project",
			nbTeams: 2,
			nbWomen: 2,
			phase: "COMPOSING",
			actual: true
		}

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await createProject(create)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: "projects",
			body: create,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const create: CreateProject = {
			name: "New Project",
			nbTeams: 2,
			nbWomen: 2,
			phase: "COMPOSING",
			actual: true
		}

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = createProject(create)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("setActualProject", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const idNewActualProject = 1

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await setActualProject(idNewActualProject)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: `projects/actual/${idNewActualProject}`
		})
	})

	it("should throw an error if the response is an error", async() => {
		const idNewActualProject = 1

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = setActualProject(idNewActualProject)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("getAllProjects", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const projects = [fakeProject(), fakeProject()]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(projects))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getAllProjects()

		// Assertions
		expect(result).toEqual(projects)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "projects",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllProjects()

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})

describe("deleteProject", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const id = 1

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await deleteProject(id)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "DELETE",
			route: `projects/${id}`
		})
	})

	it("should throw an error if the response is an error", async() => {
		const id = 1

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = deleteProject(id)

		// Assertions
		await expect(promise).rejects.toThrow("Internal Server Error")
	})
})