<script setup lang="ts">

import { useQuery } from "@tanstack/vue-query"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import Skeleton from "@/components/ui/skeleton/Skeleton.vue"
import { cn } from "@/utils/style"
import { getCurrentSprint, getGradedSprints } from "@/services/sprint"
import { formatSprintEndType } from "@/types/sprint"
import { getCurrentPhase } from "@/services/project"

defineProps<{
	modelValue: string | null
}>()

// eslint-disable-next-line func-call-spacing
const emit = defineEmits<{
	(e: "update:modelValue", value: string): void
}>()

const onValueChange = (newValue: string) => {
	emit("update:modelValue", newValue)
}

const { data: currentPhase } = useQuery({ queryKey: ["project-phase"], queryFn: getCurrentPhase })
const { data: sprints } = useQuery({
	queryKey: ["sprints"],
	queryFn: async() => {
		const sprints = await getGradedSprints()
		if (sprints && sprints.length > 0) {
			const currentSprint = getCurrentSprint(sprints)
			onValueChange(currentSprint?.id.toString() ?? sprints[0].id.toString())
		}
		return sprints
	}
})

const style = cn("w-56")

/*
let sprint = unfilteredSprints.find(sprint => {
		const startDate = new Date(sprint.startDate)
		startDate.setHours(0, 0, 0, 0)
		const endDate = new Date(sprint.endDate)
		endDate.setHours(23, 59, 59, 999)
		return startDate <= currentDate && currentDate <= endDate
	})

	// If no current sprint is found, find the next upcoming sprint
	if (!sprint) {
		sprint = unfilteredSprints.find(sprint => {
			const startDate = new Date(sprint.startDate)
			startDate.setHours(0, 0, 0, 0)
			return startDate > currentDate
		})
	}

	currentSprint.value = sprint || null
*/

</script>

<template>
<!--	<Skeleton v-if="!sprints || sprints.length === 0" :class="style" />-->

	<Select v-if="currentPhase === 'PUBLISHED'" :model-value="modelValue ?? undefined" @update:model-value="onValueChange">
		<SelectTrigger :class="style">
			<SelectValue />
		</SelectTrigger>
		<SelectContent>
			<SelectItem v-for="sprint in sprints" :key="sprint.id" :value="sprint.id.toString()">
				Sprint {{ sprint.sprintOrder }} ({{ formatSprintEndType(sprint.endType) }})
			</SelectItem>
		</SelectContent>
	</Select>
</template>