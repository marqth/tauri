import type { ExtractNameResponse } from "./string.type"

/**
 * Extract the first name and the last name from a full name
 * @param name The name with the following format: "lastName firstName"
 * @returns An object containing the first name and the last name
 */
export const extractNames = (name: string): ExtractNameResponse => {
	const spaceIndex = name.indexOf(" ")

	if (spaceIndex === -1) {
		return { firstName: name, lastName: "" }
	}

	const lastName = name.substring(0, spaceIndex).trim()
	const firstName = name.substring(spaceIndex + 1).trim()
	return { firstName, lastName }
}