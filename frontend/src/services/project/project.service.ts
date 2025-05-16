import { type ProjectPhase, type Project, ProjectSchema, type UpdateProject, UpdateProjectSchema } from "@/types/project"
import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { Cookies } from "@/utils/cookie"
import { type CreateProject, CreateProjectSchema } from "@/types/project"

export const getCurrentProject = async(): Promise<Project> => {
	const id = Cookies.getProjectId()
	if (id === null) {
		throw new Error("No project selected")
	}

	const response = await queryAndValidate({
		route: `projects/${id}`,
		responseSchema: ProjectSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const getCurrentPhase = async(): Promise<ProjectPhase> => {
	return getCurrentProject().then(project => project.phase)
}

export const updateProject = async(body: UpdateProject): Promise<void> => {
	const id = Cookies.getProjectId()
	if (id === null) {
		throw new Error("No project selected")
	}

	const response = await mutateAndValidate({
		method: "PATCH",
		route: `projects/${id}`,
		body,
		bodySchema: UpdateProjectSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}


export const createProject = async(body: CreateProject): Promise<void> => {
	const response = await mutateAndValidate({
		method: "POST",
		route: "projects",
		body,
		bodySchema: CreateProjectSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}

export const setActualProject = async(idNewActualProject: number): Promise<void> => {
	const response = await mutateAndValidate({
		method: "POST",
		route: `projects/actual/${idNewActualProject}`
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}


export const getAllProjects = async(): Promise<Project[]> => {
	const response = await queryAndValidate({
		route: "projects",
		responseSchema: ProjectSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}


export const deleteProject = async(id: number): Promise<void> => {
	const response = await mutateAndValidate({
		method: "DELETE",
		route: `projects/${id}`
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}
}