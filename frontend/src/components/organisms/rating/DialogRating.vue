<script setup lang="ts">

import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ErrorText } from "@/components/atoms/texts"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import LoadingButton from "../../molecules/buttons/LoadingButton.vue"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { ref } from "vue"
import { createGrade } from "@/services/grade/grade.service"
import { createToast } from "@/utils/toast"
import type { GradeType } from "@/types/grade-type"
import { getGradeTypeByName } from "@/services/grade-type"

let mark = ref("")
const open = ref(false)
const props = defineProps<{
	title: string,
	description : string,
	teamId : string,
	sprintId : string,
	gradeTypeString : string
}>()

const { data: gradeType, refetch } = useQuery<GradeType, Error>({
	queryKey: ["grade-type"],
	queryFn: () => getGradeTypeByName(props.gradeTypeString)
})

const { mutate, isPending, error } = useMutation({ mutationKey: ["create-grade"], mutationFn: async() => {
	await createGrade({
		value: Number(mark.value),
		gradeTypeId: gradeType.value.id,
		teamId: Number(props.teamId),
		sprintId: Number(props.sprintId),
		comment: null,
		studentId: null
	})
		.then(() => mark.value = "")
		.then(() => createToast("La note a bien été enregistrée."))
		.then(() => open.value = false)
} })

const handleNoteInput = (event: InputEvent) => {
	const inputNote = parseInt((event.target as HTMLInputElement).value)
	if (inputNote > 20) {
		mark.value = String(20)
	} else {
		mark.value = String(inputNote)
	}
	void refetch()
}

</script>

<template>
	<CustomDialog :title=title :description=description>
		<template #trigger>
			<Button variant="default">Noter une équipe</Button>
		</template>

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue.</ErrorText>

		<div class="grid gap-4 py-4">
			<div class="grid grid-cols-3 items-center gap-4">
				<Label for="note">Note :</Label>
				<Input id="note" type="number" min="0" max="20" v-model="mark" @input="handleNoteInput" v-on:blur="mutate"/>
			</div>
		</div>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<DialogClose>
				<LoadingButton type="submit" :loading="isPending">
					Confirmer
				</LoadingButton>
			</DialogClose>
		</template>
	</CustomDialog>
</template>