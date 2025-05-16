import { describe, it, expect, vi } from "vitest"
import { getTeams,
	getTeamById,
	updateTeam,
	setTeamName,
	setTeamLeader,
	generateTeams,
	getCriteria,
	getTeamAverage,
	deleteAllTeams,
	getTeamByUserId,
	getTeamByLeaderId,
	getPresentationOrder } from "./team.service"
import * as apiUtils from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { fakeTeam } from "@/factories/team.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import { createArray } from "@/utils/array"
import { fakeCriteria } from "@/factories/criteria.factory"
import { fakeStudent1 } from "@/factories/student.factory"
import { fakePresentationOrder1, fakePresentationOrder2 } from "@/factories/presentation-order.factory"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getTeams", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const data = createArray(6, i => fakeTeam({ id: i + 1 }))

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const teams = await getTeams()

		// Assertions
		expect(teams).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeams()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams: 500 Internal Server Error")
	})

	it("should throw an error if the response is invalid", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("invalid json")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeams()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to validate GET teams")
	})
})

describe("getTeamById", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const team = fakeTeam({ id: 1 })

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(team))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getTeamById(1)

		// Assertions
		expect(result).toEqual(team)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams/1",
			// eslint-disable-next-line @typescript-eslint/no-unsafe-assignment
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeamById(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams/1: 500 Internal Server Error")
	})

	it("should throw an error if the response is invalid", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("invalid json")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeamById(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to validate GET teams/1")
	})
})

describe("updateTeam", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const team = fakeTeam({ id: 1 })

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(team))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await updateTeam(1, team)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			route: "teams/1",
			method: "PATCH",
			body: team,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = updateTeam(1, fakeTeam({ id: 1 }))

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch PATCH teams/1: 500 Internal Server Error")
	})
})

describe("setTeamName", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const teamId = 1
		const newName = "New Team Name"

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await setTeamName(teamId, newName)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			route: `teams/update-name-team/${teamId}`,
			params: { newName },
			method: "PUT"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = setTeamName(1, "New Team Name")

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch PUT teams/update-name-team/1: 500 Internal Server Error")
	})
})

describe("setTeamLeader", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const teamId = 1
		const newLeader = "New Leader"

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await setTeamLeader(teamId, newLeader)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			route: `teams/update-leader-team/${teamId}`,
			params: { idLeader: newLeader },
			method: "PUT"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = setTeamLeader(1, "New Leader")

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch PUT teams/update-leader-team/1: 500 Internal Server Error")
	})
})

describe("generateTeams", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const nbTeams = "6"
		const nbWomen = "1"
		const autoWomenRatio = true

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await generateTeams(nbTeams, nbWomen, autoWomenRatio)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			route: "teams",
			method: "POST",
			params: { autoWomenRatio: autoWomenRatio.toString() },
			body: { nbTeams, nbWomen },
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = generateTeams("6", "1", true)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch POST teams: 500 Internal Server Error")
	})
})

describe("getCriteria", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const criteria = fakeCriteria()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(criteria))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getCriteria(1)

		// Assertions
		expect(result).toEqual(criteria)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams/1/criteria",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getCriteria(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams/1/criteria: 500 Internal Server Error")
	})

	it("should throw an error if the response is invalid", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("invalid json")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getCriteria(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to validate GET teams/1/criteria")
	})
})

describe("getTeamAverage", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const average = 3.5

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(average))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getTeamAverage(1)

		// Assertions
		expect(result).toEqual(average)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams/1/average",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeamAverage(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams/1/average: 500 Internal Server Error")
	})

	it("should throw an error if the response is invalid", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("invalid json")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeamAverage(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to validate GET teams/1/average")
	})
})

describe("deleteAllTeams", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await deleteAllTeams()

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "DELETE",
			route: "teams"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = deleteAllTeams()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch DELETE teams: 500 Internal Server Error")
	})
})

describe("getTeamByUserId", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const team = fakeTeam()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(team))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getTeamByUserId(1)

		// Assertions
		expect(result).toEqual(team)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "users/1/team",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeamByUserId(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET users/1/team: 500 Internal Server Error")
	})

	it("should return null if the response is an empty string", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("\"\"")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getTeamByUserId(1)

		// Assertions
		expect(result).toBeNull()
	})
})

describe("getTeamByLeaderId", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const team = fakeTeam()

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(team))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getTeamByLeaderId("1", "1")

		// Assertions
		expect(result).toEqual(team)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams/leader/1",
			params: { projectId: "1" },
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getTeamByLeaderId("1", "1")

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams/leader/1: 500 Internal Server Error")
	})
})

describe("getPresentationOrder", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const team = fakeTeam({ id: 1, name: "Team 1" })
		const student = fakeStudent1(team)
		const students = [student, student]
		const presentationOrder = [fakePresentationOrder1(student), fakePresentationOrder2(student)]


		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(presentationOrder))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getPresentationOrder(team.id.toString(), "1")

		// Assertions
		expect(result).toEqual(students)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams/1/presentation-order",
			params: { sprintId: "1" },
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getPresentationOrder("1", "1")

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams/1/presentation-order: 500 Internal Server Error")
	})
})