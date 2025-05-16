<script setup lang="ts">
import { useQuery } from "@tanstack/vue-query"
import { getAllFlags, getFlagsByConcernedTeam } from "@/services/flag"
import { Cookies } from "@/utils/cookie"
import { getStudentById } from "@/services/student"
import SwitchStudentsFlag from "@/components/organisms/teams/switch-student/SwitchStudentsFlag.vue"
import PlViewSwitchStudentsFlag from "@/components/organisms/teams/switch-student/PlViewSwitchStudentsFlag.vue"
import { Button } from "@/components/ui/button"
import { Row } from "@/components/atoms/containers"
import { ArrowLeftRight, User } from "lucide-vue-next"
import { Subtitle } from "@/components/atoms/texts"

const props = defineProps<{
	isPl: boolean
}>()

const { data: flags } = useQuery({
	queryKey: ["flagForConcerned"],
	queryFn: async() => {
		if (!props.isPl) {
			const user = await getStudentById(Cookies.getUserId())
			return getFlagsByConcernedTeam(user.team!.id)
		} else {
			const allFlags = await getAllFlags()
			return allFlags.filter(flag => flag.firstStudent !== null && flag.secondStudent !== null && flag.status === null)
		}
	}
})

</script>

<template>
	<div v-if="!props.isPl">
		<div v-for="flag in flags" :key="flag.id">
			<SwitchStudentsFlag v-if="flag.status === null" :flag="flag" :isPl="props.isPl" />
		</div>
	</div>
	<div v-else>
		<Row v-if="flags && flags.length > 0" class="gap-4 border rounded-lg p-2 md:p-6 mb-4 bg-white justify-between">
			<Row>
				<Row class="mr-2">
					<User />
					<ArrowLeftRight />
					<User />
				</Row>
				<Subtitle class="text-center w-[80%]">Demandes de modification d'équipe</Subtitle>
			</Row>
			<PlViewSwitchStudentsFlag :flags="flags" :isPl="props.isPl">
				<Button variant="outline">
					Voir les demandes
				</Button>
			</PlViewSwitchStudentsFlag>
		</Row>
	</div>
</template>