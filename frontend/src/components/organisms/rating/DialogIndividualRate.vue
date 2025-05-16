<script setup lang="ts">

import { CustomDialog } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { Column, Row } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { ref, watch } from "vue"
import { DialogClose } from "@/components/ui/dialog"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { getGradeTypeByName } from "@/services/grade-type"
import { createGrade, getGradeByGradeTypeAndAuthorAndSprint, updateGrade } from "@/services/grade"
import { createToast } from "@/utils/toast"
import { getStudentsByTeamId } from "@/services/student"
import LoadingButton from "../../molecules/buttons/LoadingButton.vue"
import { Cookies } from "@/utils/cookie"
import type { Grade } from "@/types/grade"

const open = ref(false)
const DIALOG_DESCRIPTION = "Vous pouvez attribuer une note individuelle à chaque étudiant"
const currentUserId = Cookies.getUserId()
const selectedStudentIndex = ref<number | null>(null)

const props = defineProps<{
	title: string,
	description : string,
	teamId : string,
	sprintId : string,
	gradeTypeString : string
	canGrade : boolean
	canComment : boolean
}>()

const { data: gradeType } = useQuery({ queryKey: ["grade-type", props.gradeTypeString], queryFn: () => getGradeTypeByName(props.gradeTypeString) })

const { data: teamStudents, refetch: refetchTeamStudents } = useQuery({ queryKey: ["team-students", props.teamId], queryFn: async() => {
	if (!props.teamId) return
	return await getStudentsByTeamId(Number(props.teamId), true)
} })

// Initialize studentsIndividualGrade with null values
type StudentGrade = Grade | { id: null, value: number | null, comment: string | null, studentId: number} | null
const studentsIndividualGrade: StudentGrade[] = Array<StudentGrade>(teamStudents.value?.length ?? 0).fill(null)
const studentsIndividualGradeRef: StudentGrade[] = Array<StudentGrade>(teamStudents.value?.length ?? 0).fill(null)

// Update studentsIndividualGrade with actual grades
const { refetch: refetchIndividualGrades } = useQuery({
	queryKey: ["student-grades", props.teamId, props.sprintId],
	queryFn: async() => {
		if (!teamStudents.value || !gradeType.value) return
		for (let i = 0; i < teamStudents.value.length; i++) {
			studentsIndividualGrade[i] = await getGradeByGradeTypeAndAuthorAndSprint(teamStudents.value[i].id, gradeType.value.id, currentUserId, Number(props.sprintId))
			studentsIndividualGradeRef[i] = JSON.parse(JSON.stringify(studentsIndividualGrade[i]))
		}
	}
})

const { refetch: refetchIndividualGrade } = useQuery({
	queryKey: ["student-grade", props.sprintId, selectedStudentIndex.value],
	queryFn: async() => {
		if (!teamStudents.value || !gradeType.value || !selectedStudentIndex.value) return
		studentsIndividualGrade[selectedStudentIndex.value] = await getGradeByGradeTypeAndAuthorAndSprint(teamStudents.value[selectedStudentIndex.value].id, gradeType.value.id, currentUserId, Number(props.sprintId))
		studentsIndividualGradeRef[selectedStudentIndex.value] = JSON.parse(JSON.stringify(studentsIndividualGrade[selectedStudentIndex.value]))
	}
})


watch(() => props.teamId, async() => {
	await refetchTeamStudents()
	await refetchIndividualGrades()
})
watch(() => props.sprintId, async() => await refetchIndividualGrades())

//TODO placer les watchs dans la RatingPage et exécuter tous les refetchs à cet endroit ?

//TODO Créer un grade ref pour la comparation à la fin de la fonction ?

const handleGradeInput = (event: InputEvent, index: number, inputType: "value" | "comment") => {
	if (!studentsIndividualGrade || !teamStudents.value || !gradeType.value) return

	const inputValue = (event.target as HTMLInputElement).value

	if (studentsIndividualGrade[index] === null || studentsIndividualGrade[index].id === null) {

		if (studentsIndividualGrade[index] === null) {
			studentsIndividualGrade[index] = {
				id: null,
				value: null,
				comment: null,
				studentId: teamStudents.value[index].id
			}
		}

		if (inputType === "value") {
			const value = parseFloat(inputValue)

			if (isNaN(value)) {
				studentsIndividualGrade[index].value = null
			} else if (value > 20) {
				studentsIndividualGrade[index].value = 20
			} else if (value < 0) {
				studentsIndividualGrade[index].value = 0
			} else {
				studentsIndividualGrade[index].value = value //TODO à virer avec le v-model ?
			}
		} else if (inputType === "comment") {
			if (inputValue === "") {
				studentsIndividualGrade[index].comment = null
			} else {
				studentsIndividualGrade[index].comment = inputValue //TODO à virer avec le v-model ?
			}
		}

		// Check if the new grade has nothing in it
		if (studentsIndividualGrade[index].value === null && (studentsIndividualGrade[index].comment === null || studentsIndividualGrade[index].comment === "")) { //Peu retirer le dernier critère ? //Changer pour une méthode "isBlank" globale ?
			studentsIndividualGrade[index] = null
		}

	} else  {

		if (inputType === "value") {
			const value = parseFloat(inputValue)

			if (isNaN(value)) {
				studentsIndividualGrade[index].value = null
			} else if (value > 20) {
				studentsIndividualGrade[index].value = 20
			} else if (value < 0) {
				studentsIndividualGrade[index].value = 0
			} else {
				studentsIndividualGrade[index].value = value
			}
		} else if (inputType === "comment") {
			if (inputValue === "") {
				studentsIndividualGrade[index].comment = null
			} else {
				studentsIndividualGrade[index].comment = inputValue //TODO  à virer avec le v-model ?
			}
		}
	}
}

const { mutate, isPending } = useMutation({ mutationKey: ["mutate-individual-grade", selectedStudentIndex.value], mutationFn: async(index: number) => {
	const grade = studentsIndividualGrade[index]
	if (!grade) return
	if (grade.id === null) await createGrade({ value: grade.value, comment: grade.comment, studentId: grade.studentId, gradeTypeId: gradeType.value.id, teamId: null, sprintId: Number(props.sprintId) })
		.then(() => open.value = false)
		.then(() => createToast("La note a été créée."))
		.then(() => refetchIndividualGrade())
	else if (JSON.stringify(grade) !== JSON.stringify(studentsIndividualGradeRef[index])) await updateGrade(grade.id, { value: grade.value, comment: grade.comment })
		.then(() => open.value = false)
		.then(() => createToast("La note a été mise à jour."))
		.then(() => refetchIndividualGrade()) //TODO Faire une méthode refetch unitaire pour réduire le nb de requêtes ?
} })

</script>

<template>
	<CustomDialog :title=props.title v-model="open" :description="DIALOG_DESCRIPTION" class="w-full">
		<template #trigger>
			<Button variant="default">Noter les étudiants</Button>
		</template>
		<div class="flex">
			<Row class="flex-wrap">
				<Row v-for="(student, index) in teamStudents" :key="student.id" class="mb-3 w-1/2">
					<Column>
						<Row class="grid grid-cols-[3fr,1fr] mr-2">
							<Label :for="student.name" class="whitespace-nowrap mt-3">{{ student.name }}</Label>
							<Input v-if="props.canGrade" v-on:blur="mutate(index)" class="mb-2 " type="number" :default-value="studentsIndividualGrade[index]?.value ?? ''" min="0" max="20" @click="selectedStudentIndex = index" @input="handleGradeInput($event, index, 'value')" />
						</Row>
						<Row class="mr-2">
							<Input v-if="props.canComment" v-on:blur="mutate(index)" type="text" :default-value="studentsIndividualGrade[index]?.comment ?? ''" @click="selectedStudentIndex = index" @input="handleGradeInput($event, index, 'comment')" />
						</Row>
					</Column>
				</Row>
			</Row>
		</div>
		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" :loading="isPending" @click="mutate">
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>
<style scoped>
	.Row {
		display: flex;
		flex-wrap: wrap;
	}

	.Row input[type="number"] {
		min-width: 500px;
	}/* Adjust as needed */
</style>