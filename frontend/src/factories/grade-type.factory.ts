import { fakeProject } from "@/factories/project.factory"
import type { GradeType } from "@/types/grade-type"

export const fakeGradeType = (): GradeType => {
	return {
		id: 1,
		name: "Grade Type",
		factor: 1,
		forGroup: true,
		imported: true,
		project: fakeProject(),
		scaleTXTBlob: "scaleTXTBlob"
	}
}