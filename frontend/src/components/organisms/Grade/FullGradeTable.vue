<script setup lang="ts">

import {
	Blocks,
	LucideCircleFadingPlus,
	LucideCirclePlus,
	Play,
	Presentation,
	SquareGanttChart,
	User,
	Users
} from "lucide-vue-next"
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Row } from "@/components/atoms/containers"
import { cn } from "@/utils/style"
import { ref, watch } from "vue"
import type { Bonus } from "@/types/bonus"
import { useQuery } from "@tanstack/vue-query"
import { getStudentsByTeamId } from "@/services/student"
import { getStudentBonuses } from "@/services/bonus"
import {
	getIndividualTotalGrades,
	getSprintGrades,
	getStudentsAverageByTeam,
	getTeamAverage,
	getTeamTotalGrade
} from "@/services/grade"
import { GradeTooltip } from "@/components/molecules/tooltip"
import { getGradeFormula } from "@/types/grade-type"
import { Cookies } from "@/utils/cookie"

const rowClass = cn("py-2 h-auto mt-2 mb-2 ")
const cellClass = cn("py-2 h-auto mt-2 mb-2 text-center")
const gradeConfirmed = cn("bg-green-100")
const gradeNotConfirmed = cn("bg-red-100")

const props = defineProps<{
	teamId : string,
	sprintId : string,
	isGradesConfirmed: boolean
}>()

const studentBonuses = ref<Bonus[][] | null>(null)
const role = Cookies.getRole()
let oldTeamId = ""
let oldSprint = ""

const { data: teamStudents, ...queryTeamStudents } = useQuery({
	queryKey: ["team-students", props.teamId],
	queryFn: async() => {
		if (props.teamId === "") return
		const students = await getStudentsByTeamId(Number(props.teamId), true)
		studentBonuses.value = await Promise.all(students.map(student => getStudentBonuses(student.id, props.sprintId)))
		return students
	}
})

const { data: averageTeam, ...queryAverageTeam } = useQuery({
	queryKey: ["average", "team", props.teamId, props.sprintId],
	queryFn: () => getTeamAverage(Number(props.teamId), props.sprintId)
})

const { data: averageStudents, ...queryAverageStudent } = useQuery({
	queryKey: ["average", "student", props.teamId, props.sprintId],
	queryFn: async() => {
		return await getStudentsAverageByTeam(Number(props.teamId), props.sprintId)
	}
})

const { data: sprintGrades, ...querySprintGrades } = useQuery({
	queryKey: ["sprint-grades", props.teamId, props.sprintId],
	queryFn: () => getSprintGrades(Number(props.teamId), Number(props.sprintId))
})

const { data: totalGrade, ...queryTotalGrade } = useQuery({
	queryKey: ["totalGrade", props.teamId, props.sprintId],
	queryFn: () => getTeamTotalGrade(Number(props.teamId), Number(props.sprintId))
})

const { data: totalIndividualGrades, ...queryTotalIndividualGrades } = useQuery({
	queryKey: ["individual", props.teamId, props.sprintId],
	queryFn: () => getIndividualTotalGrades(Number(props.teamId), Number(props.sprintId))
})

watch(() => props.teamId, async() => {
	if (props.teamId !== oldTeamId) {
		await queryTeamStudents.refetch()
		await queryAverageTeam.refetch()
		await queryAverageStudent.refetch()
		await querySprintGrades.refetch()
		await queryTotalGrade.refetch()
		await queryTotalIndividualGrades.refetch()
		oldTeamId = props.teamId
	}
})

watch(() => props.sprintId, async() => {
	if (props.sprintId !== oldSprint) {
		await queryTeamStudents.refetch()
		await queryAverageTeam.refetch()
		await queryAverageStudent.refetch()
		await querySprintGrades.refetch()
		await queryTotalGrade.refetch()
		await queryTotalIndividualGrades.refetch()
		oldSprint = props.sprintId
	}
})

</script>

<template>
	<Table>
		<TableHeader>
			<TableRow>
				<TableHead :class="rowClass">Nom</TableHead>
				<TableHead :class="rowClass" title="Solution technique">
					<div class="flex items-center justify-center">
						<Blocks :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" title="Gestion de projet">
					<div class="flex items-center justify-center">
						<SquareGanttChart :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" title="Conformité au sprint">
					<div class="flex items-center justify-center">
						<Play :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" title="Contenu de présentation">
					<div class="flex items-center justify-center">
						<Presentation :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass">
					<div class="flex items-center justify-center">
						<Row>
							<div class="mr-2">Total équipe</div>
							<GradeTooltip :formula="getGradeFormula('TE')" />
						</Row>
					</div>
				</TableHead>
				<TableHead :class="rowClass" title="Bonus / Malus limités">
					<div class="flex items-center justify-center">
						<LucideCircleFadingPlus :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" title="Bonus / Malus illimités">
					<div class="flex items-center justify-center">
						<LucideCirclePlus :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" >
					<Row>
						<div class="mr-2 flex items-center justify-center">Total Bonus</div>
						<GradeTooltip :formula="getGradeFormula('TB')" />
					</Row>
				</TableHead>
				<TableHead :class="rowClass" title="Performance globale de l'équipe">
					<div class="flex items-center justify-center">
						<Users :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" title="Performance individuelle">
					<div class="flex items-center justify-center">
						<User :stroke-width="1"/>
					</div>
				</TableHead>
				<TableHead :class="rowClass" >
					<Row>
						<div class="mr-2 flex items-center justify-center">Total individuel</div>
						<GradeTooltip :formula="getGradeFormula('TI')" />
					</Row>
				</TableHead>
				<TableHead :class="rowClass" >
					<Row>
						<div class="mr-2 flex items-center justify-center">Note finale</div>
						<GradeTooltip :formula="getGradeFormula('NF')" />
					</Row>
				</TableHead>
			</TableRow>
		</TableHeader>
		<TableBody>
			<TableRow v-for="(student, index) in teamStudents" :key="student.id">
				<TableCell class="font-medium" :class="cn(cellClass, 'text-left')">{{student.name}}</TableCell>
				<TableCell :class="cellClass">{{averageTeam ? averageTeam["Solution Technique"] : ''}} </TableCell>
				<TableCell :class="cellClass">{{averageTeam ? averageTeam["Gestion de projet"] : ''}}</TableCell>
				<TableCell :class="cellClass">{{averageTeam ? averageTeam["Conformité au sprint"] : ''}}</TableCell>
				<TableCell :class="cellClass">{{averageTeam ? averageTeam["Contenu de la présentation"] : ''}}</TableCell>
				<TableCell :class="cellClass"> {{totalGrade ? totalGrade : ''}} </TableCell>
				<TableCell :class="cellClass">{{ studentBonuses ? studentBonuses[index][1].value : ''}} </TableCell>
				<TableCell :class="cellClass">{{ (studentBonuses && isGradesConfirmed) ? studentBonuses[index][0].value : '' }} </TableCell>
				<TableCell v-if="studentBonuses " :class="cellClass">  {{ (studentBonuses[index][1].value ? studentBonuses[index][1].value : 0) + ((studentBonuses[index][0].value && isGradesConfirmed) ? studentBonuses[index][0].value : 0) }} </TableCell>
				<TableCell v-if="averageTeam" :class="cellClass"> {{averageTeam["Performance globale de l'équipe"]}} </TableCell>
				<TableCell v-if="averageStudents" :class="cn(cellClass, isGradesConfirmed ? gradeConfirmed : gradeNotConfirmed )">{{ (role === 'OPTION_STUDENT' && !isGradesConfirmed) ? '' : averageStudents[student.id]?.toFixed(2)}}</TableCell>
				<TableCell v-if="totalIndividualGrades" :class="cellClass"> {{totalIndividualGrades[index].toPrecision(4) ? totalIndividualGrades[index] : 0}} </TableCell>
				<TableCell v-if="sprintGrades" :class="cellClass"> {{sprintGrades[index]}} </TableCell>
			</TableRow>
		</TableBody>
	</Table>
</template>