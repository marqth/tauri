<script setup lang="ts">

import { Table, TableBody, TableCell, TableRow, TableHead, TableHeader } from "@/components/ui/table"
import { AccordionContent } from "@/components/ui/accordion"
import { getCriteria, getTeamAverage } from "@/services/team"
import { extractNames } from "@/utils/string"
import { CheckIcon, GenderIcon } from "@/components/atoms/icons"
import { useQuery } from "@tanstack/vue-query"
import { PageSkeleton } from "@/components/atoms/skeletons"
import { Column, Row } from "@/components/atoms/containers"
import { Subtitle, Text } from "@/components/atoms/texts"
import { cn } from "@/utils/style"
import type { Student } from "@/types/student"
import { GripVertical } from "lucide-vue-next"
import { hasPermission } from "@/services/user"
import { getAllImportedGrades } from "@/services/grade"
import { Skeleton } from "@/components/ui/skeleton"

const props = defineProps<{
	teamId: number
	students: Student[] | null,
}>()

const { data: criteria } = useQuery({ queryKey: ["criteria", props.teamId], queryFn: () => getCriteria(props.teamId) })
const { data: average } = useQuery({ queryKey: ["average", props.teamId], queryFn: () => getTeamAverage(props.teamId) })
const { data: grades } = useQuery({ queryKey: ["grades"], queryFn: getAllImportedGrades })


const rowClass = cn("py-2 h-auto")

const handleDragStart = (event: DragEvent, itemData: Student) => {
	event.dataTransfer?.setData("text/plain", JSON.stringify(itemData))
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
}

const canDragAndDrop = hasPermission("TEAM_MANAGEMENT")

</script>

<template>
	<PageSkeleton v-if="!students || !criteria || average === undefined" />
	<AccordionContent v-else class="w-full flex mb-4 items-start gap-12">
		<Table class="flex-1">
			<TableHeader>
				<TableRow>
					<TableHead :class="rowClass" class="w-1" v-if="canDragAndDrop"></TableHead>
					<TableHead :class="rowClass" class="min-w-28">Nom</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Prénom</TableHead>
					<TableHead :class="rowClass" class="min-w-16">Genre</TableHead>
					<TableHead :class="rowClass" class="min-w-16">Bachelor</TableHead>
					<TableHead :class="rowClass" class="min-w-16">Moyenne</TableHead>
				</TableRow>
			</TableHeader>

			<TableBody
				v-if="students"
			>
				<TableRow
					v-for="student in students" :key="student.id"
					:draggable="canDragAndDrop" v-on:dragstart="(e: DragEvent) => handleDragStart(e, student)"
				>
					<TableCell :class="rowClass" v-if="canDragAndDrop">
						<GripVertical class="h-4 cursor-move" />
					</TableCell>
					<TableCell :class="rowClass">{{ extractNames(student.name).lastName }}</TableCell>
					<TableCell :class="rowClass">{{ extractNames(student.name).firstName }}</TableCell>
					<TableCell :class="rowClass">
						<GenderIcon :gender="student.gender" />
					</TableCell>
					<TableCell :class="rowClass">
						<CheckIcon :checked="student.bachelor" />
					</TableCell>
					<Skeleton v-if="!grades" class="w-5/6 h-5" />
					<TableCell v-else class="py-0">
						{{grades?.find(grade => grade.student?.id === student.id && grade.gradeType.name === 'Moyenne')?.value?.toPrecision(4) ?? "" }}
					</TableCell>
				</TableRow>
			</TableBody>
		</Table>

		<Column v-if="criteria" class="w-auto border rounded px-4 py-3 min-w-56">
			<Subtitle class="mb-1">Critères de génération</Subtitle>
			<Row class="gap-1 items-center">
				<CheckIcon :checked="criteria.validCriteriaWoman" />
				<Text>Nombre de femmes : {{ criteria.nbWomens }}</Text>
			</Row>
			<Row class="gap-1 items-center">
				<CheckIcon :checked="criteria.validCriteriaBachelor" />
				<Text>Nombre de bachelors : {{ criteria.nbBachelors }}</Text>
			</Row>
			<Row class="gap-1 items-center" v-if="average">
				<CheckIcon :checked="true" />
				<Text>Moyenne : {{ average.toPrecision(4) }}</Text>
			</Row>
			<Row class="gap-1 items-center">
				<CheckIcon :checked="students.length > 0" />
				<Text>Nombre d'étudiants : {{ students.length }}</Text>
			</Row>
		</Column>
	</AccordionContent>
</template>