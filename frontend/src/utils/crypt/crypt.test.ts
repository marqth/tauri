import { expect, test } from "vitest"
import { decrypt, encrypt } from "."


test("encrypt and decrypt", () => {
	expect(
		decrypt(
			encrypt("test")
		)
	).toBe("test")

	expect(
		JSON.parse(
			decrypt(
				encrypt(
					JSON.stringify({ test: "test" })
				)
			)
		)
	).toStrictEqual({ test: "test" })

	expect(
		JSON.parse(
			decrypt(
				encrypt(
					JSON.stringify([])
				)
			)
		)
	).toStrictEqual([])
})