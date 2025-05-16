import { mutateAndValidate, queryAndValidate } from "@/utils/api"
import { CreateValidationFlagSchema, ValidationFlagSchema } from "@/types/validationFlag"
import type { ValidationFlag } from "@/types/validationFlag"

export const getValidationFlagsByFlagId = async(flagId: number): Promise<ValidationFlag[]> => {
	const response = await queryAndValidate({
		route: `flags/${flagId}/validation`,
		responseSchema: ValidationFlagSchema.array()
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}

export const updateValidationFlag = async(flagId: number, authorId: number, confirmed: boolean) => {
	const response = await mutateAndValidate({
		method: "PATCH",
		route: `flags/${flagId}/validation/${authorId}`,
		body: { flagId, authorId, confirmed },
		bodySchema: CreateValidationFlagSchema
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response
}