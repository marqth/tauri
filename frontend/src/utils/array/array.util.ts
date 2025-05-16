export const createArray = <T>(length: number, callback: (index: number) => T): T[] => {
	return Array.from({ length }).map((_, i) => callback(i))
}