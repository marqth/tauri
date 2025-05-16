<script setup lang="ts">

import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Row } from "@/components/atoms/containers"
import { cn } from "@/utils/style"
import { useQuery } from "@tanstack/vue-query"
import { getTeams } from "@/services/team"
import { getAverageSprintGrades, getSprintGrades } from "@/services/grade"
import { watch } from "vue"
import { GradeTooltip } from "@/components/molecules/tooltip"
import { getGradeFormula } from "@/types/grade-type"

const rowClass = cn("py-2 h-auto mt-2 mb-2")
let oldTeamId = ""

const props = defineProps<{
	teamId : string,
	sprintId : string,
}>()

const { data: teams, ...queryTeams } = useQuery({ queryKey: ["teams"], queryFn: getTeams })
const { data: sprintGrades, ...querySprintGrades } = useQuery({
	queryKey: ["sprint-grades", props.teamId, props.sprintId],
	queryFn: () => getSprintGrades(Number(props.teamId), Number(props.sprintId))
})

const { data: averageSprintGrades, ...queryAverageSprintGrades } = useQuery({
	queryKey: ["averageSprintGrade", props.sprintId],
	queryFn: () => getAverageSprintGrades(Number(props.sprintId))
})

watch(() => props.teamId, async() => {
	if (props.teamId !== oldTeamId) {
		await queryTeams.refetch()
		await querySprintGrades.refetch()
		await queryAverageSprintGrades.refetch()
		oldTeamId = props.teamId
	}
})
</script>

<template>
	<Table>
		<TableHeader>
			<TableRow>
				<TableHead :class="rowClass" >Nom</TableHead>
				<TableHead :class="rowClass">
					<div class="flex items-center justify-center">
						<Row>
							<div class="mr-2">Note finale</div>
							<GradeTooltip :formula="getGradeFormula('NF')"/>
						</Row>
					</div>
				</TableHead>
			</TableRow>
		</TableHeader>
		<TableBody>
			<TableRow v-for="team in teams" :key="team.id">
				<TableCell class="font-medium" :class="rowClass">{{team.name}}</TableCell>
				<TableCell v-if="sprintGrades && averageSprintGrades" :class="rowClass"> {{averageSprintGrades[team.id - 1]}}</TableCell>
			</TableRow>
		</TableBody>
	</Table>
</template>

<style scoped>

</style>