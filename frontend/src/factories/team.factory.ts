import { fakeProject } from "@/factories/project.factory"
import { fakeUser } from "@/factories/user.factory"
import type { Team } from "@/types/team"

let teamIdCounter = 1

export const fakeTeam = (props?: Partial<Team>): Team => {
	const team: Team = {
		id: props?.id ?? teamIdCounter,
		name: props?.name ?? "Team " + teamIdCounter,
		leader: props?.leader ?? fakeUser(),
		project: props?.project ?? fakeProject()
	}

	teamIdCounter++

	return team
}