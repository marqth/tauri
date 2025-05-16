<script setup lang="ts">

import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import { Column } from "@/components/atoms/containers"
import { cn } from "@/utils/style"
import { InfoText } from "@/components/atoms/texts"
import type { Sprint } from "@/types/sprint"
import { extractNames } from "@/utils/string"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { getPresentationOrder, getTeamByUserId, updatePresentationOrder } from "@/services/team"
import { Cookies } from "@/utils/cookie"
import { GripVertical } from "lucide-vue-next"
import type { Student } from "@/types/student"
import { ref } from "vue"
import { createToast } from "@/utils/toast"

const props = defineProps<{
	sprint: Sprint
}>()

const userId = Cookies.getUserId()
const dragState = ref<number | null>(null)
const dragOrder = ref<number | null>(null)
const students = ref<Student[] | undefined>(undefined)

const { refetch } = useQuery({
	queryKey: ["presentation-order-current-team", props.sprint.id],
	queryFn: async() => {
		const team = await getTeamByUserId(userId)
		if (!team) return undefined
		students.value = await getPresentationOrder(team.id.toString(), props.sprint.id.toString())
		return students.value
	}
})

const { mutate } = useMutation({
	mutationFn: async() => {
		if (!students.value) return
		const team = await getTeamByUserId(userId)
		if (!team) return
		await updatePresentationOrder(team.id.toString(), props.sprint.id.toString(), students.value)
			.then(() => createToast("L'ordre de passage a été mis à jour avec succès."))
			.then(() => refetch())
	}
})

const rowClass = cn("py-2 h-auto")

const handleDrop = async(event: DragEvent, order: number) => {
	event.preventDefault()
	dragOrder.value = null

	if (!students.value || !dragState.value) return

	const student = students.value[dragState.value - 1]
	if (order === dragState.value) return

	let newStudents = students.value.filter(s	=> s.id !== student.id)
	if (order === 0) {
		newStudents = [student, ...newStudents]
	} else {
		newStudents.splice(order, 0, student)
	}

	students.value = newStudents

	void mutate()
}

const handleDragStart = (event: DragEvent, itemData: Student, index: number) => {
	event.dataTransfer?.setData("text/plain", JSON.stringify(itemData))
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
	dragState.value = index
}

const handleDragEnter = (event: DragEvent, order: number) => {
	event.preventDefault()
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
	dragOrder.value = order
}

const handleDragLeave = (event: DragEvent) => {
	event.preventDefault()
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
	dragOrder.value = null
}


</script>

<template>
	<Column class="flex-1">
		<Table class="flex-1">
			<TableHeader>
				<TableRow
					v-on:drop="(e: DragEvent) => handleDrop(e, 0)"
					v-on:dragenter="(e: DragEvent) => handleDragEnter(e, 0)"
					v-on:dragover="(e: DragEvent) => handleDragEnter(e, 0)" v-on:dragleave="handleDragLeave"
					:class="cn({ 'border-b-light-blue': dragOrder === 0 && dragState !== 1 })"
				>
					<TableHead :class="rowClass" class="w-1"></TableHead>
					<TableHead :class="rowClass" class="w-20">Ordre</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Nom</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Prénom</TableHead>
				</TableRow>
			</TableHeader>

			<TableBody>
				<TableRow v-for="(student, i) in students" :key="student.id"
					draggable="true" v-on:dragstart="(e: DragEvent) => handleDragStart(e, student, i + 1)"
					v-on:drop="(e: DragEvent) => handleDrop(e, i + 1)"
					v-on:dragenter="(e: DragEvent) => handleDragEnter(e, i + 1)"
					v-on:dragover="(e: DragEvent) => handleDragEnter(e, i + 1)" v-on:dragleave="handleDragLeave"
					:class="cn({ 'border-b-light-blue': dragOrder === i + 1 && dragState !== i + 1 && dragState !== i + 2 })"
				>
					<TableCell :class="rowClass">
						<GripVertical class="h-4 cursor-move" />
					</TableCell>
					<TableCell :class="rowClass">{{ i + 1 }}</TableCell>
					<TableCell :class="rowClass">{{ extractNames(student.name).lastName }}</TableCell>
					<TableCell :class="rowClass">{{ extractNames(student.name).firstName }}</TableCell>
				</TableRow>

				<TableRow />
			</TableBody>
		</Table>
		<InfoText class="text-center italic mt-2">Ordre de passage</InfoText>
	</Column>
</template>