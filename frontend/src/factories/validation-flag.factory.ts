import type { ValidationFlag } from "@/types/validationFlag"
import { fakeUser } from "@/factories/user.factory"
import { fakeFlag } from "@/factories/flag.factory"


export const fakeValidationFlag = (): ValidationFlag => {
	return {
		author: fakeUser(),
		flag: fakeFlag(),
		confirmed: true
	}
}