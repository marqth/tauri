<script setup lang="ts">

import { ErrorText, InfoText, Subtitle } from "@/components/atoms/texts"
import { Column, Row } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { useMutation, useQueryClient } from "@tanstack/vue-query"
import { createOrUpdateGrade, getRatedGrade } from "@/services/grade"
import { createToast } from "@/utils/toast"
import { onMounted, ref, watch } from "vue"
import { CheckIcon, Loader } from "@/components/atoms/icons"
import { getGradeTypeDescription, type GradeTypeName } from "@/types/grade-type"
import { Button } from "@/components/ui/button"
import { downloadGradeScaleTXT } from "@/services/grade-type"
import { getGradeTypeByName } from "@/services/grade-type"
import type { Grade } from "@/types/grade"

const props = defineProps<{
	gradeTypeName: GradeTypeName,
	sprintId: string,
	teamId?: string,
	studentId?: string,
	allGrades: Grade[],
	gradeAuthorization: boolean,
	commentAuthorization: boolean
}>()

const queryClient = useQueryClient()
const status = ref<"IDLE" | "LOADING" | "DONE">("IDLE")
const grade = ref("")
const comment = ref("")
const oldValues = ref({ grade: "", comment: "" })
const isGradeScaleUploaded = ref(false)

const updateGrade = () => {
	const data = getRatedGrade(props.allGrades, {
		sprintId: Number(props.sprintId),
		teamId: props.teamId ? Number(props.teamId) : null,
		studentId: null,
		gradeTypeName: props.gradeTypeName
	})

	grade.value = data?.value?.toString() ?? ""
	comment.value = data?.comment ?? ""
	oldValues.value = { grade: grade.value, comment: comment.value }

	status.value = "IDLE"
}

const checkGradeScaleUploaded = async() => {
	try {
		const gradeType = await getGradeTypeByName(props.gradeTypeName)
		isGradeScaleUploaded.value = !!gradeType.scaleTXTBlob // Check if the grade scale is present
	} catch (error) {
		console.error("Error checking grade scale:", error)
		isGradeScaleUploaded.value = false
	}
}

const { mutate, isPending, isError } = useMutation({
	mutationFn: async() => {
		if (grade.value === oldValues.value.grade && comment.value === oldValues.value.comment) {
			return
		}

		status.value = "LOADING"
		await createOrUpdateGrade({
			value: Number(grade.value),
			comment: comment.value,
			sprintId: Number(props.sprintId),
			teamId: props.teamId ? Number(props.teamId) : null,
			studentId: props.studentId ? Number(props.studentId) : null,
			gradeTypeName: props.gradeTypeName
		})
			.then(() => createToast("La note a bien été enregistrée."))
			.then(() => oldValues.value = { grade: grade.value, comment: comment.value })
			.then(() => queryClient.invalidateQueries({ queryKey: ["all-rated-grades"] }))
			.then(() => queryClient.invalidateQueries({ queryKey: ["notifications"] }))
	} })

const onGradeChange = (value: string | number) => {
	const parsedValue = Number(value)
	if (value === "" || isNaN(parsedValue)) {
		grade.value = ""
		return
	}
	if (parsedValue > 20) {
		grade.value = "20"
		return
	}
	if (parsedValue < 0) {
		grade.value = "0"
		return
	}
	grade.value = parsedValue.toString()
}

watch(isPending, (newValue) => {
	if (!newValue && status.value === "LOADING") {
		status.value = "DONE"
	}
})

watch(status, (newValue) => {
	if (newValue === "DONE") {
		setTimeout(() => {
			status.value = "IDLE"
		}, 5000)
	}
})

watch(() => [props.teamId, props.sprintId], () => {
	updateGrade()
})

onMounted(() => {
	updateGrade()
	checkGradeScaleUploaded()
})

const download = useMutation({
	mutationFn: async() => {
		await downloadGradeScaleTXT(props.gradeTypeName)
			.then(() => createToast("Le fichier a été téléchargé."))
	},
	onError: () => createToast("Erreur lors du téléchargement du fichier.")
})

</script>

<template>
	<Column class="items-stretch justify-start gap-3 border rounded-md p-6 bg-white">
		<Row class="items-center justify-between gap-2">
			<Row class="items-center justify-start gap-2">
				<slot />
				<Subtitle>{{ gradeTypeName }}</Subtitle>
			</Row>
			<Loader v-if="status === 'LOADING'" class="size-4 stroke-slate-500" />
			<CheckIcon v-if="status === 'DONE'" :checked="!isError" />
		</Row>

		<Row class="items-center justify-between gap-6">
			<InfoText class="flex-1">{{ getGradeTypeDescription(gradeTypeName) }}</InfoText>
			<Input v-if="gradeAuthorization" class="w-16" type="number" min="0" max="20" v-model="grade"
				@update:model-value="onGradeChange" v-on:blur="mutate" :disabled="isPending" />
		</Row>

		<Textarea v-if="commentAuthorization" placeholder="Ajouter un commentaire" v-model="comment"
			:disabled="isPending" v-on:blur="mutate" />

		<ErrorText v-if="status === 'DONE' && isError">Une erreur est survenue.</ErrorText>

		<Row class="items-center justify-end mt-4" v-if="isGradeScaleUploaded">
			<Button variant="outline" @click="download.mutate">
				Télécharger le barème
			</Button>
		</Row>
	</Column>
</template>