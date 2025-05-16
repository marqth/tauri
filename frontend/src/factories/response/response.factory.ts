import type { FakeResponseRequest } from "@/factories/response/response.type"

export const fakeResponse = ({ ok = true, text = "", status = 200, statusText = "" }: FakeResponseRequest): Response => {
	return {
		text: () => Promise.resolve(text),
		ok,
		status,
		statusText,
		headers: new Headers(),
		redirected: false,
		type: "basic",
		url: "",
		body: null,
		bodyUsed: false,
		clone: () => new Response(),
		arrayBuffer: () => Promise.resolve(new ArrayBuffer(0)),
		blob: () => Promise.resolve(new Blob()),
		formData: () => Promise.resolve(new FormData()),
		json: () => Promise.resolve(null)
	}
}

export const fakeResponse200 = (text: string): Response => fakeResponse({ text })

export const fakeResponse500 = (statusText: string): Response => fakeResponse({ ok: false, status: 500, statusText })