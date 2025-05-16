<script setup lang="ts">

import { SidebarTemplate } from "@/components/templates"
import { Error, NotAuthorized } from "@/components/organisms/errors"
import { Header } from "@/components/molecules/header"
import { useQuery } from "@tanstack/vue-query"
import { getSprints } from "@/services/sprint/sprint.service"
import { ref } from "vue"
import type { CalendarDate } from "@internationalized/date"
import { PageSkeleton } from "@/components/atoms/skeletons"
import { dateToCalendarDate } from "@/utils/date"
import { hasPermission } from "@/services/user"
import { Column } from "@/components/atoms/containers"
import { AddSprint, SprintNotCreated, SprintSection } from "@/components/organisms/sprints"

const lastSprintOrder = ref<number>(0)
const lastSprintEndDate = ref<CalendarDate | undefined>()

const { data: sprints, error, refetch: refetchSprints, isLoading, isFetching } = useQuery({ queryKey: ["sprints"], queryFn: async() => {
	const sprints = await getSprints()

	if (sprints.length > 0) {
		lastSprintOrder.value = sprints[sprints.length - 1].sprintOrder
		lastSprintEndDate.value = dateToCalendarDate(sprints[sprints.length - 1].endDate)
	}

	return sprints
} })

const canEditSprints = hasPermission("MANAGE_SPRINT")
const canViewSprints = hasPermission("SPRINTS_PAGE")

</script>

<template>
	<SidebarTemplate>
		<Header title="Sprints" />
		<PageSkeleton v-if="isLoading || isFetching" />

		<Column v-else-if="canViewSprints" class="gap-4 h-full">
			<SprintSection v-for="sprint in sprints" :key="sprint.id" :can-edit-sprints="canEditSprints"
				:sprint="sprint" @edit:sprint="refetchSprints" @delete:sprint="refetchSprints"
			/>
			<AddSprint v-if="canEditSprints"
				:first-sprint="!sprints || sprints.length === 0" @add:sprint="refetchSprints"
				:lastSprintOrder="lastSprintOrder" :lastSprintEndDate="lastSprintEndDate"
			/>
			<SprintNotCreated v-else-if="!sprints || sprints.length === 0" />
		</Column>

		<NotAuthorized v-else />
		<Error v-if="error" />
	</SidebarTemplate>
</template>