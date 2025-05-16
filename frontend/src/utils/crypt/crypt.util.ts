import CryptoJS from "crypto-js"


const importKey = (): string => {
	const key = import.meta.env.VITE_CRYPT_KEY
	if (!key) {
		console.error("Crypto key not found")
		throw new Error("Crypto key not found")
	}
	return key
}

export const encrypt = (decryptedData: string): string => {
	const key = importKey()
	return CryptoJS.AES.encrypt(decryptedData, key).toString()
}

export const decrypt = (encryptedData: string): string => {
	const key = importKey()
	return CryptoJS.AES.decrypt(encryptedData, key).toString(CryptoJS.enc.Utf8)
}