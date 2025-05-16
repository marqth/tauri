<script setup lang="ts">

import { SidebarTemplate } from "@/components/templates"
import { StudentsTable, DeleteStudentsDialog, ImportStudents, ExportStudents, AddStudentDialog } from "@/components/organisms/students"
import { Error, NotAuthorized } from "@/components/organisms/errors"
import { Button } from "@/components/ui/button"
import { GradeFactorsDialog } from "@/components/organisms/students"
import { getAllStudents } from "@/services/student"
import { Header } from "@/components/molecules/header"
import { computed } from "vue"
import { getAllImportedGradeTypes } from "@/services/grade-type/grade-type.service"
import { getAllImportedGrades } from "@/services/grade"
import { useQuery } from "@tanstack/vue-query"
import { hasPermission } from "@/services/user"
import { getCurrentPhase } from "@/services/project/project.service"

const { data: currentPhase } = useQuery({ queryKey: ["project-phase"], queryFn: getCurrentPhase })
const { data: students, ...studentsQuery } = useQuery({ queryKey: ["students"], queryFn: getAllStudents })
const { data: gradeTypes, ...gradeTypesQuery } = useQuery({ queryKey: ["gradeTypes"], queryFn: getAllImportedGradeTypes })
const { data: grades, ...gradesQuery } = useQuery({ queryKey: ["grades"], queryFn: getAllImportedGrades })

const refetch = async() => {
	await studentsQuery.refetch()
	await gradeTypesQuery.refetch()
	await gradesQuery.refetch()
}

const buttonVariant = computed(() => {
	return (currentPhase.value === "PREPUBLISHED" || currentPhase.value ===  "PUBLISHED") ? "default" : "outline"
})

const authorized = hasPermission("STUDENTS_PAGE")
const displayButtons = computed(() => authorized && students.value && students.value.length > 0)
const canEdit = hasPermission("EDIT_IMPORTED_GRADE_TYPES")
const canExport = hasPermission("EXPORT_STUDENT_LIST") && hasPermission("EXPORT_INDIVIDUAL_GRADES")

</script>

<template>
	<SidebarTemplate>
		<Header title="Étudiants">
			<DeleteStudentsDialog v-if="displayButtons && currentPhase === 'COMPOSING'" @delete:students="refetch">
				<Button variant="outline">Supprimer les étudiants</Button>
			</DeleteStudentsDialog>
			<GradeFactorsDialog
				v-if="displayButtons && gradeTypes && currentPhase === 'COMPOSING' && canEdit"
				:grade-types="gradeTypes" @update:factors="refetch">
				<Button variant="outline">Modifier les coefficients</Button>
			</GradeFactorsDialog>
			<ExportStudents v-if="displayButtons && canExport">
				<Button :variant="buttonVariant">Exporter</Button>
			</ExportStudents>
			<AddStudentDialog v-if="displayButtons  && currentPhase  === 'COMPOSING' && canEdit" @add:student="refetch">
				<Button variant="default">Ajouter un étudiant</Button>
			</AddStudentDialog>
		</Header>

		<NotAuthorized v-if="!authorized" />
		<ImportStudents v-else-if="authorized && students && students.length === 0" @import:students="refetch" />
		<StudentsTable v-else-if="authorized" :students="students ?? null" :grade-types="gradeTypes ?? null" :grades="grades ?? null" @delete:student="refetch" @update:student="refetch"/>
		<Error v-else />
	</SidebarTemplate>
</template>