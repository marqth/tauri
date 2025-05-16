import type { z } from "zod"

export type QueryAndValidateRequest<T> = {
	route: string
	params?: Record<string, string>
	jsonContent?: boolean
	delay?: number
	responseSchema: z.ZodType<T>
}

export type QueryAndValidateResponse<T> = {
	status: "success"
	data: T
} | {
	status: "error"
	error: string
}

export type MutateAndValidateRequest<T> = {
	method: "POST" | "PUT" | "PATCH" | "DELETE"
	route: string
	params?: Record<string, string>
	jsonContent?: boolean
	delay?: number
	bodySchema?: z.ZodType<T>
	body?: T
}

export type MutateAndValidateResponse = {
	status: "success"
} | {
	status: "error"
	error: string
}


export type LoginAndValidateRequest<T, R> = {
	route: string
	bodySchema?: z.ZodType<T>
	body?: T
	responseSchema: z.ZodType<R>
}

export type LoginAndValidateResponse<R> = {
	status: "success"
	data: R
} | {
	status: "error"
	error: string
}