<script setup lang="ts">

import { type Notification } from "@/types/notification"
import NotificationElement from "@/components/molecules/notifications/NotificationElement.vue"
import { changeStateChecked } from "@/services/notification/notification.service"

const emits = defineEmits(["open:notifications", "read:notifications"])

const searchLinkByType = (notification: Notification) => {
	const types = {
		CREATE_TEAMS: "/teams",
		BONUS_MALUS: "/grades",
		MOVE_STUDENT: "/teams",
		DELETE_STUDENTS: "/students",
		IMPORT_STUDENTS: "/students",
		CREATE_GRADE: "/grades"
	}
	return types[notification.type]
}

defineProps<{
	notifications: Notification[] | null
}>()

const markNotificationAsRead = async(notificationId: number) => {
	await changeStateChecked(notificationId)
		.then(() => emits("read:notifications"))
}
</script>

<template>
	<div v-for="(notification, i) in notifications" :key="i" class="w-full">
		<NotificationElement
			:title="notification.userFrom.name" :description="notification.message ?? ''" :link="searchLinkByType(notification)"
			@read:notifications="markNotificationAsRead(notification.id)" class="w-full bg-white"
		/>
	</div>
</template>