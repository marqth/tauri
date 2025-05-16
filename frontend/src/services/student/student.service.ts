import { type CreateStudent, type Student, type UpdateStudent } from "@/types/student"
import { StudentSchema } from "@/types/student"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { z } from "zod"
import { Cookies } from "@/utils/cookie"

export const getAllStudents = async(): Promise<Student[]> => {
	const response = await queryAndValidate({
		responseSchema: StudentSchema.array(),
		route: "students"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getStudentById = async(id: number): Promise<Student> => {
	const response = await queryAndValidate({
		route: `students/${id}`,
		responseSchema: StudentSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data

}

export const getStudentsByTeamId = async(teamId: number, ordered: boolean): Promise<Student[]> => {
	const response = await queryAndValidate({
		route: `teams/${teamId}/students`,
		params: { ordered: ordered.toString() },
		responseSchema: StudentSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const importStudentFile = async(file: File): Promise<void> => {
	const formData = new FormData()
	formData.append("file-upload", file)

	const response = await mutateAndValidate({
		method: "POST",
		route: "students/upload",
		body: formData,
		bodySchema: z.instanceof(FormData),
		jsonContent: false
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const deleteAllStudents = async(): Promise<void> => {
	const response = await mutateAndValidate({
		method: "DELETE",
		route: "students"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const deleteStudent = async(id : number): Promise<void> => {
	const response = await mutateAndValidate({
		method: "DELETE",
		route: `students/${id.toString()}`
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const createStudent = async(body: Omit<CreateStudent, "privateKey" | "email" | "password">): Promise<void> => {
	const currentProjectId = Cookies.getProjectId()
	const response = await mutateAndValidate({
		method: "POST",
		route: "students",
		body: {
			...body,
			email: "",
			password: "",
			privateKey: "",
			projectId: currentProjectId
		},
		bodySchema: z.unknown()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

}

export const updateStudent = async(id: string | null, body: UpdateStudent): Promise<void> => {
	const response = await mutateAndValidate({
		method: "PATCH",
		route: `students/${id}`,
		body,
		bodySchema: z.any()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const downloadStudentFile = async(): Promise<void> => {
	const response = await queryAndValidate({
		route: "students/download",
		responseSchema: z.string()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	const url = window.URL.createObjectURL(new Blob([response.data]))
	const link = document.createElement("a")
	link.href = url
	link.setAttribute("download", "students.csv")
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)
}