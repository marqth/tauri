import { describe, it, expect, vi } from "vitest"
import { getAllStudents,
	getStudentById,
	getStudentsByTeamId,
	importStudentFile,
	deleteAllStudents,
	updateStudent,
	downloadStudentFile } from "./student.service"
import * as apiUtils from "@/utils/api"
import { fakeStudent1 } from "@/factories/student.factory"
import { fakeResponse200, fakeResponse500 } from "@/factories/response"
import type { UpdateStudent } from "@/types/student"
import { StudentSchema } from "@/types/student"
import { Cookies } from "@/utils/cookie"
import { fakeTeam } from "@/factories/team.factory"

global.fetch = vi.fn()
vi.spyOn(Cookies, "getProjectId").mockReturnValue(1)
vi.spyOn(Cookies, "getToken").mockReturnValue("token")
vi.spyOn(apiUtils, "queryAndValidate")
vi.spyOn(apiUtils, "mutateAndValidate")

describe("getAllStudents", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const team = fakeTeam()
		const data = [fakeStudent1(team), fakeStudent1(team)]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(data))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const students = await getAllStudents()

		// Assertions
		expect(students).toEqual(data)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "students",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getAllStudents()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET students: 500 Internal Server Error")
	})
})

describe("getStudentById", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const student = fakeStudent1(fakeTeam())

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(student))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getStudentById(1)

		// Assertions
		expect(result).toEqual(student)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "students/1",
			responseSchema: StudentSchema
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getStudentById(1)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET students/1: 500 Internal Server Error")
	})
})

describe("getStudentsByTeamId", () => {
	it("should call queryAndValidate with correct arguments", async() => {
		const team = fakeTeam()
		const students = [fakeStudent1(team), fakeStudent1(team)]

		// Setup mock response
		const mockResponse = fakeResponse200(JSON.stringify(students))
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const result = await getStudentsByTeamId(1, false)

		// Assertions
		expect(result).toEqual(students)
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "teams/1/students",
			responseSchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = getStudentsByTeamId(1, false)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET teams/1/students: 500 Internal Server Error")
	})
})

describe("importStudentFile", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		// Setup mock file
		const file = new File(["content"], "test.csv", { type: "text/csv" })

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await importStudentFile(file)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "POST",
			route: "students/upload",
			body: expect.any(FormData),
			bodySchema: expect.any(Object),
			jsonContent: false
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock file
		const file = new File(["content"], "test.csv", { type: "text/csv" })

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = importStudentFile(file)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch POST students/upload: 500 Internal Server Error")
	})
})

describe("deleteAllStudents", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await deleteAllStudents()

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "DELETE",
			route: "students"
		})
	})

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = deleteAllStudents()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch DELETE students: 500 Internal Server Error")
	})
})

describe("updateStudent", () => {
	it("should call mutateAndValidate with correct arguments", async() => {
		const student: UpdateStudent = {
			name: "New Name",
			email: "new.email@example.com",
			teamId: 1
		}

		// Setup mock response
		const mockResponse = fakeResponse200("")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await updateStudent("1", student)

		// Assertions
		expect(apiUtils.mutateAndValidate).toHaveBeenCalledWith({
			method: "PATCH",
			route: "students/1",
			body: student,
			bodySchema: expect.any(Object)
		})
	})

	it("should throw an error if the response is an error", async() => {
		const student: UpdateStudent = {
			name: "New Name",
			email: "new.email@example.com",
			teamId: 1
		}

		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = updateStudent("1", student)

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch PATCH students/1: 500 Internal Server Error")
	})
})

describe("downloadStudentFile", () => {
	/*it("should call queryAndValidate with correct arguments", async() => {
		// Setup mock response
		const mockCreateObjectURL = vi.fn(() => "blobURL")
		vi.spyOn(window.URL, "createObjectURL").mockImplementation(mockCreateObjectURL)
		const mockResponse = fakeResponse200("file content")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		await downloadStudentFile()

		// Assertions
		expect(apiUtils.queryAndValidate).toHaveBeenCalledWith({
			route: "students/download",
			responseSchema: expect.any(Object)
		})
	})*/

	it("should throw an error if the response is an error", async() => {
		// Setup mock response
		const mockResponse = fakeResponse500("Internal Server Error")
		vi.mocked(fetch).mockResolvedValueOnce(mockResponse)

		// Call the function
		const promise = downloadStudentFile()

		// Assertions
		await expect(promise).rejects.toThrow("Failed to fetch GET students/download: 500 Internal Server Error")
	})
})