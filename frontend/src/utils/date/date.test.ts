import { expect, test } from "vitest"
import { formatDate, serializeDate } from "."


test("serializeDate", () => {
	expect(serializeDate(new Date("2021-01-01"))).toBe("2021-01-01")

	expect(serializeDate(new Date("2021-12-31"))).toBe("2021-12-31")

	expect(serializeDate(new Date("2021-12-31T23:59:59"))).toBe("2021-12-31")

	expect(serializeDate(new Date("2021-12-31T23:59:59.999"))).toBe("2021-12-31")

	expect(serializeDate(new Date("2021-12-31T23:59:59.999+02:00"))).toBe("2021-12-31")

	expect(serializeDate(new Date("2021-12-31T23:59:59.999-02:00"))).toBe("2022-01-01")
})


test("formatDate", () => {
	expect(formatDate(new Date("2021-01-01"))).toBe("01/01/2021")

	expect(formatDate(new Date("2021-12-31"))).toBe("31/12/2021")

	expect(formatDate(new Date("2021-12-31T23:59:59"))).toBe("31/12/2021")

	expect(formatDate(new Date("2021-12-31T23:59:59.999"))).toBe("31/12/2021")

	expect(formatDate(new Date("2021-12-31T23:59:59.999+02:00"))).toBe("31/12/2021")

	expect(formatDate(new Date("2021-12-31T23:59:59.999-02:00"))).toBe("01/01/2022")
})