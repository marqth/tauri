import type { Sprint } from "@/types/sprint"
import { fakeProject } from "@/factories/project.factory"

export const fakeSprint = (): Sprint => {
	return {
		id: 1,
		startDate: new Date(),
		endDate: new Date(),
		endType: "NORMAL_SPRINT",
		sprintOrder: 1,
		project: fakeProject()
	}
}