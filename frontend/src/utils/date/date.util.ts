import { CalendarDate } from "@internationalized/date"
import { format } from "date-fns"

export const serializeDate = (date: Date): string => {
	return format(date, "yyyy-MM-dd")
}

export const serializeCalendarDate = (date: CalendarDate): string => {
	return format(date.toDate("UTC"), "yyyy-MM-dd")
}

export const formatDate = (date: Date): string => {
	return format(date, "dd/MM/yyyy")
}

export const dateToCalendarDate = (date: Date): CalendarDate => {
	const day = String(date.getDate()).padStart(2, "0")
	const month = String(date.getMonth() + 1).padStart(2, "0")
	const year = date.getFullYear()
	return new CalendarDate(Number(year), Number(month), Number(day))
}