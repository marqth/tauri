<script setup lang="ts">

import {
	Sheet, SheetContent,
	SheetHeader,
	SheetTitle,
	SheetTrigger
} from "@/components/ui/sheet"
import { Header } from "@/components/molecules/header"
import { useQuery } from "@tanstack/vue-query"
import { getAllNotificationsFromUser } from "@/services/notification/notification.service"
import NotificationTable from "@/components/organisms/notifications/NotificationTable.vue"
import { Text } from "@/components/atoms/texts"
import { Cookies } from "@/utils/cookie"

const currentUserId = Cookies.getUserId()
const { data: notifications, refetch } = useQuery({ queryKey: ["notifications"], queryFn: async() => {
	const notifications = await getAllNotificationsFromUser(currentUserId)
	return notifications.filter(notification => !notification.checked)
} })

</script>

<template>
	<Sheet key="left">
		<SheetTrigger asChild>
			<slot />
		</SheetTrigger>
		<SheetContent side="left" class="flex flex-col items-stretch justify-start">
			<SheetHeader>
				<SheetTitle>
					<Header title="Notifications">
					</Header>
				</SheetTitle>
			</SheetHeader>
			<div class="flex-1 w-full overflow-y-scroll">
				<Text v-if="!notifications || notifications.length === 0">Vous n'avez aucune notification pour le moment</Text>
				<NotificationTable v-else :notifications="notifications" @read:notifications="refetch" />
			</div>
		</SheetContent>
	</Sheet>
</template>