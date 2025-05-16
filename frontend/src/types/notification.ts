import { z } from "zod"
import { UserSchema } from "@/types/user"

export const NotificationSchema = z.object({
	id: z.number(),
	message: z.string().optional().nullable(),
	checked: z.boolean().optional().nullable(),
	type: z.enum(["CREATE_TEAMS", "BONUS_MALUS", "MOVE_STUDENT", "DELETE_STUDENTS", "IMPORT_STUDENTS", "CREATE_GRADE"]),
	userTo: UserSchema,
	userFrom: UserSchema
})

export type Notification = z.infer<typeof NotificationSchema>

const NotificationType = NotificationSchema.pick({ type: true })

export type NotificationType = z.infer<typeof NotificationType>;

export const CreateNotificationSchema = z.object({
	message: z.string().optional().nullable(),
	checked: z.boolean().optional().nullable(),
	type: z.string().optional().nullable(),
	userToId: z.coerce.number(),
	userFromId: z.coerce.number()
})

export type CreateNotification = z.infer<typeof CreateNotificationSchema>