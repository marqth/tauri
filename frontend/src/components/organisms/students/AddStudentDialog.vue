<script setup lang="ts">

import { ErrorText } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { createToast } from "@/utils/toast"
import { createStudent } from "@/services/student"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { ref, watch } from "vue"
import { Row } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { cn } from "@/utils/style"
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Switch } from "@/components/ui/switch"
import { createGrade } from "@/services/grade"
import { getUserByName } from "@/services/user"
import { Label } from "@/components/ui/label"

const open = ref(false)
const rowClass = cn("grid grid-cols-2 items-center mb-2 justify-between")
const gendered = ref("")
const grade = ref("")
const lastName = ref("")
const firstName = ref("")
let fullName = ref("")
const bachelor = ref(false)
const emits = defineEmits(["add:student"])


const DIALOG_TITLE = "Ajout d'un étudiant"
const DIALOG_DESCRIPTION = "Vous pouvez ajouter un étudiant en remplissant les champs suivants."


const { mutate: addStudent, isPending, error } = useMutation({ mutationKey: ["add-student"], mutationFn: async() => {
	await createStudent({
		name: lastName.value + " " +  firstName.value,
		gender: gendered.value,
		bachelor: bachelor.value
	})
	await createGrade({
		value: Number(grade.value),
		teamId: null,
		sprintId: null,
		studentId: (await getUserByName(fullName.value)).id,
		comment: null,
		gradeTypeName: "Moyenne"
	})
		.then(() => grade.value = "")
		.then(() => lastName.value = "")
		.then(() => firstName.value = "")
		.then(() => gendered.value = "")
		.then(() => grade.value = "")
		.then(() => bachelor.value = false)
		.then(() => emits("add:student"))
		.then(() => createToast("L'étudiant a bien été ajouté."))
		.then(() => open.value = false)
} })

const onGradeChange = (value: string | number) => {
	if (Number(value) > 20) {
		grade.value = "20"
		return
	}
	if (Number(value) < 0) {
		grade.value = "0"
		return
	}
	grade.value = value.toString()
}

watch([lastName, firstName], () => {
	fullName.value = lastName.value + " " + firstName.value
	useQuery({ queryKey: ["user"], queryFn: () => getUserByName(fullName.value) })
})

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<ErrorText v-if="error">Une erreur est survenue.</ErrorText>
		<Row :class="rowClass">
			<Label>Nom : </Label>
			<Input v-model="lastName" class="w-full"/>
		</Row>
		<Row :class="rowClass">
			<Label>Prénom : </Label>
			<Input v-model="firstName" class="w-full"/>
		</Row>
		<Row :class="rowClass">
			<Label>Genre : </Label>
			<Select v-model="gendered">
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
				<Switch id="Bachelor" :checked="bachelor" @update:checked="value => bachelor = value"/>
			</div>
		</Row>
		<Row :class="rowClass">
			<Label>Moyenne :</Label>
			<Input  type="number" min="0" max="20"  @update:model-value="onGradeChange"/>
		</Row>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="addStudent" :loading="isPending">
				Ajouter
			</LoadingButton>
		</template>
	</CustomDialog>
</template>