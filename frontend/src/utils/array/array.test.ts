import { expect, test } from "vitest"
import { createArray } from "."


test("create array", () => {
	expect(createArray(3, i => i)).toEqual([0, 1, 2])

	expect(createArray(0, i => i)).toEqual([])

	expect(createArray(5, i => i)).toEqual([0, 1, 2, 3, 4])

	expect(createArray(3, () => "a")).toEqual(["a", "a", "a"])

	expect(createArray(3, i => i + 1)).toEqual([1, 2, 3])

	expect(createArray(3, i => i * 2)).toEqual([0, 2, 4])

	expect(createArray(3, i => i % 2)).toEqual([0, 1, 0])
})