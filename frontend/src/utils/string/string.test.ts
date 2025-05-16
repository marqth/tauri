import { expect, test } from "vitest"
import { extractNames } from "."

test("extractNames", () => {
	expect(extractNames("TANGUY Quentin")).toEqual({
		firstName: "Quentin",
		lastName: "TANGUY"
	})

	expect(extractNames("TANGUY  Quentin")).toEqual({
		firstName: "Quentin",
		lastName: "TANGUY"
	})

	expect(extractNames("TANGUY Quentin Quentin")).toEqual({
		firstName: "Quentin Quentin",
		lastName: "TANGUY"
	})

	expect(extractNames("Quentin")).toEqual({
		firstName: "Quentin",
		lastName: ""
	})

	expect(extractNames("")).toEqual({
		firstName: "",
		lastName: ""
	})
})