import type { RoleType } from "@/types/role"
import type { CookieName } from "./cookie.type"
import { redirect } from "@/utils/router"
import type { PermissionType } from "@/types/permission"
import { decrypt, encrypt } from "@/utils/crypt"

const COOKIE_EXPIRATION = 60 * 60 * 24 * 7 // in seconds

/**
 * Get the value of a cookie by its name
 * @param name The name of the cookie to get
 * @returns The value of the cookie if it exists, otherwise the user is redirected to the login page
 */
const getCookie = <T = string>(name: CookieName): T => {
	const cookies = document.cookie
	const index = cookies.search(name)

	if (index === -1) {
		redirect("/login")
		return null as unknown as T
	}

	// remove the ';' at the end of the cookie
	let end = cookies.indexOf(";", index)
	end = end === -1 ? cookies.length : end
	const value = cookies.slice(index + name.length + 1, end)

	return decrypt(value) as T
}

/**
 * Set a cookie with a name and a value
 * @param name The name of the cookie
 * @param value The value of the cookie
 */
const setCookie = (name: CookieName, value: string): void => {
	const encryptedValue = encrypt(value)
	document.cookie = `${name}=${encryptedValue}; path=/; max-age=${COOKIE_EXPIRATION}`
}

/**
 * Remove a cookie by its name
 * @param name The name of the cookie to remove
 */
const removeCookie = (name: CookieName): void => {
	document.cookie = `${name}=; expires=Thu, 19 Apr 2001 00:00:00 UTC; path=/;`
}


export const Cookies = {

	getRole: (): RoleType => getCookie<RoleType>("role"),
	setRole: (role: RoleType): void => setCookie("role", role),

	getToken: (): string => getCookie("token"),
	setToken: (token: string): void => setCookie("token", token),

	getProjectId: (): number => getCookie<number>("currentProject"),
	setProjectId: (id: number): void => setCookie("currentProject", id.toString()),

	getUserId: (): number => getCookie<number>("user"),
	setUserId: (id: number): void => setCookie("user", id.toString()),

	getPermissions: (): PermissionType[] => getCookie<PermissionType[]>("permissions"),
	setPermissions: (permissions: PermissionType[]): void => setCookie("permissions", JSON.stringify(permissions)),

	removeAll: (): void => {
		removeCookie("role")
		removeCookie("token")
		removeCookie("currentProject")
		removeCookie("user")
		removeCookie("permissions")
	}

}