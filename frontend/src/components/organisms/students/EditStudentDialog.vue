<script setup lang="ts">

import { ErrorText } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { createToast } from "@/utils/toast"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { ref, watch } from "vue"
import { Row } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { cn } from "@/utils/style"
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import type { Gender, Student } from "@/types/student"
import type { Grade } from "@/types/grade"
import { getUserByName } from "@/services/user"
import { updateGrade } from "@/services/grade"

const props = defineProps<{
	student: Student
	mark: Grade | null
}>()

const open = ref(false)
const rowClass = cn("grid grid-cols-2 items-center mb-2 justify-between")
const gendered = ref<Gender | "">(props.student.gender)
const newGrade = ref("")
let nameParts = props.student.name.split(" ")
let firstName = nameParts.slice(1).join(" ")
let lastName = nameParts[0]
let fullName = ref(props.student.name)
const bachelor = ref(props.student.bachelor)
const emits = defineEmits(["update:student"])


const DIALOG_TITLE = "Modification d'un étudiant"
const DIALOG_DESCRIPTION = "Vous pouvez modifier un étudiant en remplissant les champs suivants."


const { mutate, isPending, error } = useMutation({ mutationKey: ["add-student"], mutationFn: async() => {
	if (!props.mark) return

	await updateGrade(Number(props.mark.id), {
		value: Number(newGrade.value),
		teamId: null,
		sprintId: null,
		studentId: props.student.id,
		comment: null
	})
		.then(() => newGrade.value = "")
		.then(() => lastName = "")
		.then(() => firstName  = "")
		.then(() => gendered.value = "")
		.then(() => bachelor.value = false)
		.then(() => emits("update:student"))
		.then(() => createToast("L'étudiant a bien été modifié."))
		.then(() => open.value = false)
} })

const onGradeChange = (value: string | number) => {
	if (Number(value) > 20) {
		newGrade.value = "20"
		return
	}
	if (Number(value) < 0) {
		newGrade.value = "0"
		return
	}
	newGrade.value = value.toString()
}

watch([lastName, firstName], () => {
	fullName.value = lastName + " " + firstName
	useQuery({ queryKey: ["user"], queryFn: () => getUserByName(fullName.value) })
})

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot/>
		</template>

		<ErrorText v-if="error">Une erreur est survenue.</ErrorText>
		<Row :class="rowClass">
			<Label>Nom : </Label>
			<Input v-model="lastName" class="w-full" disabled/>
		</Row>
		<Row :class="rowClass">
			<Label>Prénom : </Label>
			<Input v-model="firstName" class="w-full" disabled/>
		</Row>
		<Row :class="rowClass">
			<Label>Genre : </Label>
			<Select v-model="gendered" disabled>
				<SelectTrigger >
					<SelectValue placeholder="Genre" />
				</SelectTrigger>
				<SelectContent>
					<SelectGroup>
						<SelectItem value='MAN'>Homme</SelectItem>
						<SelectItem value='WOMAN'>Femme</SelectItem>
					</SelectGroup>
				</SelectContent>
			</Select>
		</Row>
		<Row :class="rowClass">
			<Label>Bachelor :</Label>
			<div class="flex justify-end">
				<Switch id="Bachelor" :checked="bachelor ?? false" @update:checked="value => bachelor = value" disabled/>
			</div>
		</Row>
		<Row :class="rowClass" v-if="mark">
			<Label>Moyenne :</Label>
			<!-- eslint-disable-next-line vue/no-mutating-props -->
			<Input type="number" min="0" max="20" @update:model-value="onGradeChange" v-model="mark.value" />
		</Row>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutate" :loading="isPending">
				Modifier
			</LoadingButton>
		</template>
	</CustomDialog>
</template>