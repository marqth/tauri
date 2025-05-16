import { fakeUser } from "@/factories/user.factory"

export const fakeNotification = (): Notification => {
	return {
		id: 1,
		message: "Test",
		checked: true,
		type: "Notification",
		userTo: fakeUser(),
		userFrom: fakeUser()
	}
}