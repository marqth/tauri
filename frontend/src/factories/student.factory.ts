import type { Student } from "@/types/student"
import { fakeProject } from "@/factories/project.factory"
import type { Team } from "@/types/team"

export const fakeStudent1 = (team: Team): Student => {
	return {
		id: 1,
		name: "student1",
		email: "student1@tauri.com",
		password: "password",
		privateKey: "privateKey",
		gender: "MAN",
		bachelor: false,
		teamRole: "role",
		project: fakeProject(),
		team
	}
}

export const fakeStudent2 = (team: Team): Student => {
	return {
		id: 2,
		name: "student2",
		email: "student2@tauri.com",
		password: "password",
		privateKey: "privateKey",
		gender: "MAN",
		bachelor: false,
		teamRole: "role",
		project: fakeProject(),
		team: team
	}
}