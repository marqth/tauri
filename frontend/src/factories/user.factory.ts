import type { User } from "@/types/user"

export const fakeUser = (): User => {
	return {
		id: 1,
		name: "user",
		email: "user@tauri.com",
		password: "password",
		privateKey: "privateKey"
	}
}