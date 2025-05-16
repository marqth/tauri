<script setup lang="ts">

import { useQuery } from "@tanstack/vue-query"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { getTeamByUserId, getTeams } from "@/services/team"
import Skeleton from "@/components/ui/skeleton/Skeleton.vue"
import type { Team } from "@/types/team"
import { Cookies } from "@/utils/cookie"
import { cn } from "@/utils/style"
import { getCurrentPhase } from "@/services/project"

const userId = Cookies.getUserId()

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

const { data: teams } = useQuery({
	queryKey: ["teams"],
	queryFn: async() => {
		const teams = await getTeams()
		if (teams && teams.length > 0) onValueChange(teams[0].id.toString())
		return teams
	}
})

const { data: myTeam } = useQuery({
	queryKey: ["my-team", userId],
	queryFn: async() => {
		const myTeam = await getTeamByUserId(userId)
		if (myTeam) onValueChange(myTeam.id.toString())
		return myTeam
	}
})

const getTeamName = (team: Team) => {
	if (myTeam.value?.id === team.id) {
		let name = team.name ?? `Équipe ${team.id}`
		return name + " (Mon équipe)"
	}
}

const style = cn("w-56")

</script>

<template>

	<Select v-if="currentPhase === 'PUBLISHED'" :model-value="modelValue ?? undefined" @update:model-value="onValueChange">
		<SelectTrigger :class="style">
			<SelectValue />
		</SelectTrigger>
		<SelectContent>
			<SelectItem :value="myTeam.id.toString()" v-if="myTeam">{{ getTeamName(myTeam) }}</SelectItem>
			<SelectItem value="autres équipes">Autres équipes</SelectItem>
		</SelectContent>
	</Select>
</template>