import type { PresentationOrder } from "@/types/presentation-order"
import { fakeSprint } from "@/factories/sprint.factory"
import type { Student } from "@/types/student"
import { fakeProject } from "@/factories/project.factory"

export const fakePresentationOrder1 =  (student: Student): PresentationOrder => {
	return {
		sprint: fakeSprint(fakeProject()),
		student: student,
		value: 1
	}
}

export const fakePresentationOrder2 =  (student: Student): PresentationOrder => {
	return {
		sprint: fakeSprint(fakeProject()),
		student: student,
		value: 2
	}
}