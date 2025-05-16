import type { Flag } from "@/types/flag"
import { fakeUser } from "@/factories/user.factory"

export const fakeFlag = (): Flag => {
	return {
		id: 1,
		description: "TEST",
		type: "REPORTING",
		firstStudent: null,
		secondStudent: null,
		author: fakeUser(),
		status: null
	}
}