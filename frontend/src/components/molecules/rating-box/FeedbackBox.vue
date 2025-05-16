<script setup lang="ts">

import { ErrorText, InfoText, Subtitle } from "@/components/atoms/texts"
import { Column, Row } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import { createOrUpdateGrade, getRatedGrade } from "@/services/grade"
import { createToast } from "@/utils/toast"
import { onMounted, ref, watch } from "vue"
import { CheckIcon, Loader } from "@/components/atoms/icons"
import { getGradeTypeDescription, type GradeTypeName } from "@/types/grade-type"
import type { Grade } from "@/types/grade"
import { getCommentsByTeamAndSprintAndAuthor } from "@/services/feedback"
import type { Feedback } from "@/types/feedback"
import { Cookies } from "@/utils/cookie"

const props = defineProps<{
	sprintId: string,
	teamId: string,
	commentReadingAuthorization: boolean,
	commentWritingAuthorization: boolean,
	feedbackReadingAuthorization: boolean,
	feedbackWritingAuthorization: boolean,
}>()

const queryClient = useQueryClient()
const status = ref<"IDLE" | "LOADING" | "DONE">("IDLE")
const comment = ref("")
const feedback = ref("")
const oldValues = ref({ comment: "", feedback: "" })

const updateComments = () => {
	const comments = useQuery<Feedback[]>({ queryKey: ["feedbacks", props.teamId, props.sprintId, Cookies.getUserId()],
		queryFn: () => getCommentsByTeamAndSprintAndAuthor(props.teamId, props.sprintId) })

	if (comments.data.value?.length === 0 || !comments.data.value) {
		comment.value = ""
		feedback.value = ""
	} else if (comments.data.value?.length === 1) {
		if (comments.data.value[0].feedback) {
			comment.value = ""
			feedback.value = comments.data.value[0].content
		} else {
			comment.value = comments.data.value[0].content
			feedback.value = ""
		}
	} else {
		comment.value = comments.data.value[0].content
		feedback.value = comments.data.value[1].content
	}
	oldValues.value = { comment: comment.value, feedback: feedback.value }
	status.value = "IDLE"
	return
}

const { mutate, isPending, isError } = useMutation({ mutationFn: async() => {
	if (comment.value === oldValues.value.comment && feedback.value === oldValues.value.feedback) {
		return
	}
	status.value = "LOADING"
	/*await createOrUpdateComment({
		//value: grade.value !== "" ? Number(grade.value) : null,
		comment: comment.value !== "" ? comment.value : null,
		sprintId: Number(props.sprintId),
		teamId: props.teamId ? Number(props.teamId) : null,
		//studentId: props.studentId ? Number(props.studentId) : null,
		//gradeTypeName: props.gradeTypeName
	})
		.then(() => createToast("La note a bien été enregistrée."))
		//.then(() => oldValues.value = { grade: grade.value, comment: comment.value })
		.then(() => queryClient.invalidateQueries({ queryKey: ["all-rated-grades"] }))*/
} })

const onGradeChange = (value: string | number) => {
	const parsedValue = Number(value)
	if (value === "" || isNaN(parsedValue)) {
		//grade.value = ""
		return
	}
	if (parsedValue > 20) {
		//grade.value = "20"
		return
	}
	if (parsedValue < 0) {
		//grade.value = "0"
		return
	}
	//grade.value = parsedValue.toString()
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
	updateComments()
})

onMounted(() => {
	updateComments()
})

</script>

<template>
	<Column class="items-stretch justify-start gap-3 border rounded-md p-6 bg-white">
		<Row class="items-center justify-between gap-2">
			<Row class="items-center justify-start gap-2">
				<slot />
				<Subtitle>Commentaires et Feedbacks</Subtitle>
			</Row>
			<Loader v-if="status === 'LOADING'" class="size-4 stroke-slate-500" />
			<CheckIcon v-if="status === 'DONE'" :checked="!isError" />
		</Row>

		<!--<Row class="items-center justify-between gap-6">
			<InfoText class="flex-1">{{ getGradeTypeDescription(gradeTypeName) }}</InfoText>
			<Input v-if="gradeAuthorization" class="w-16" type="number" min="0" max="20" v-model="grade" @update:model-value="onGradeChange" v-on:blur="mutate" :disabled="isPending" />
		</Row>-->

		<Textarea v-if="commentReadingAuthorization" :disabled="!commentWritingAuthorization || isPending" placeholder="Ajouter un commentaire" v-model="comment" v-on:blur="mutate" />
		<Textarea v-if="feedbackReadingAuthorization" :disabled="!feedbackWritingAuthorization || isPending" placeholder="Ajouter un feedback" v-model="feedback" v-on:blur="mutate" />

		<ErrorText v-if="status === 'DONE' && isError">Une erreur est survenue.</ErrorText>
	</Column>
</template>