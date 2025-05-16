<script setup lang="ts">

import { Table, TableBody, TableCell, TableRow, TableHead, TableHeader } from "@/components/ui/table"
import { Skeleton } from "@/components/ui/skeleton"
import { cn } from "@/utils/style"
import CheckIcon from "@/components/atoms/icons/CheckIcon.vue"
import GenderIcon from "@/components/atoms/icons/GenderIcon.vue"
import type { GradeType } from "@/types/grade-type"
import type { Student } from "@/types/student"
import type { Grade } from "@/types/grade"
import { Loading } from "@/components/organisms/loading"
import { extractNames } from "@/utils/string"
import EditStudentDialog from "@/components/organisms/students/EditStudentDialog.vue"
import { Trash2, Pencil  } from "lucide-vue-next"
import DeleteStudentDialog from "@/components/organisms/students/DeleteStudentDialog.vue"
import { Row } from "@/components/atoms/containers"
import { useQuery } from "@tanstack/vue-query"
import { getCurrentPhase } from "@/services/project"
import { hasPermission } from "@/services/user"


const rowClass = cn("py-2 h-auto")

const emit = defineEmits(["delete:student", "update:student"])
const { data: currentPhase } = useQuery({ queryKey: ["project-phase"], queryFn: getCurrentPhase })


defineProps<{
	students: Student[] | null
	gradeTypes: GradeType[] | null
	grades: Grade[] | null
}>()

const canEdit = hasPermission("EDIT_IMPORTED_GRADE_TYPES")


</script>

<template>
	<div class="border bg-white rounded-md">
		<Table v-if="gradeTypes">
			<TableHeader class="h-fit">
				<TableRow class="h-10 pb-1">
					<TableHead :class="rowClass" class="min-w-36">Nom</TableHead>
					<TableHead :class="rowClass" class="min-w-36">Prénom</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Genre</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Bachelor</TableHead>
					<TableHead v-for="gradeType in gradeTypes" :key="gradeType.id" :class="rowClass" class="min-w-32">
						<span v-if="gradeType.name === 'Moyenne'">Moyenne</span>
						<span v-else>{{ gradeType.name }} ({{ gradeType.factor }})</span>
					</TableHead>
					<TableHead :class="rowClass"></TableHead>
				</TableRow>
			</TableHeader>
			<TableBody v-if="students" >
				<TableRow v-for="student in students" :key="student.id">
					<TableCell class="font-medium min-w-36" :class="rowClass">
						{{ extractNames(student.name).lastName }}
					</TableCell>
					<TableCell class="font-medium min-w-36" :class="rowClass">
						{{ extractNames(student.name).firstName }}
					</TableCell>
					<TableCell class="min-w-28" :class="rowClass">
						<GenderIcon :gender="student.gender" />
					</TableCell>
					<TableCell class="min-w-28" :class="rowClass">
						<CheckIcon :checked="student.bachelor" />
					</TableCell>
					<TableCell v-for="gradeType in gradeTypes" :key="gradeType.id" :class="rowClass" class="min-w-32">
						<Skeleton v-if="!grades" class="w-5/6 h-5" />
						<span v-else>
							{{ grades?.find(grade => grade.student?.id === student.id && grade.gradeType.id === gradeType.id)?.value?.toPrecision(4) ?? "" }}
						</span>
					</TableCell>
					<TableCell :class="rowClass">
						<Row class="items-center gap-1">
							<EditStudentDialog v-if="currentPhase  === 'COMPOSING' && canEdit" @update:student="emit('update:student')" :student="student" :mark="grades?.find(grade => grade.student?.id === student.id && grade.gradeType.name === 'Moyenne') ?? null">
								<Pencil class="stroke-gray-600 mr-2 h-4 w-4 hover:stroke-primary transition-colors" />
							</EditStudentDialog>
							<DeleteStudentDialog  v-if="currentPhase  === 'COMPOSING' && canEdit" :student="student" @delete:student="emit('delete:student')">
								<Trash2 class="stroke-gray-600 mr-2 h-4 w-4 hover:stroke-primary transition-colors" />
							</DeleteStudentDialog>
						</Row>
					</TableCell>
				</TableRow>
			</TableBody>

			<TableBody v-else>
				<TableRow v-for="i in 10" :key="i">
					<TableCell :class="rowClass">
						<Skeleton class="w-5/6 h-5" />
					</TableCell>
					<TableCell :class="rowClass">
						<Skeleton class="w-5/6 h-5" />
					</TableCell>
					<TableCell :class="rowClass">
						<Skeleton class="w-5/6 h-5" />
					</TableCell>
					<TableCell v-for="gradeType in gradeTypes" :key="gradeType.id" :class="rowClass">
						<Skeleton class="w-5/6 h-5" />
					</TableCell>
					<TableCell :class="rowClass">
						<Skeleton class="w-5/6 h-5" />
					</TableCell>
				</TableRow>
			</TableBody>
		</Table>
		<Loading class="min-h-24" v-else />
	</div>
</template>