import type { Project } from "@/types/project"

export const fakeProject = (): Project => {
	return {
		id: 1,
		nbTeams: 6,
		nbWomen: 1,
		phase: "COMPOSING",
		actual: true,
		name: "Project 1"
	}
}