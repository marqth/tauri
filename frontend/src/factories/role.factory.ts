import { type Role } from "@/types/role"
import { fakeUser } from "@/factories/user.factory"

export const fakeRole = (): Role => {
	return {
		id: 1,
		type: "PROJECT_LEADER",
		user: fakeUser()
	}
}