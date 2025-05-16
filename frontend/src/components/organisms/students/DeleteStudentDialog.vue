<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { deleteStudent } from "@/services/student/student.service"
import LoadingButton from "@/components/molecules/buttons/LoadingButton.vue"
import { ref } from "vue"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { createToast } from "@/utils/toast"
import type { Student } from "@/types/student"

const props = defineProps<{
	student: Student
}>()
const open = ref(false)
const emits = defineEmits(["delete:student"])

const { mutate, isPending, error } = useMutation({ mutationKey: ["delete-students"], mutationFn: async() => {
	await deleteStudent(props.student.id)
		.then(() => open.value = false)
		.then(() => emits("delete:student"))
		.then(() => createToast("L'étudiant a été supprimé."))
} })

const DIALOG_TITLE = "Supprimer un étudiant"
const DIALOG_DESCRIPTION = "Êtes-vous bien sûr de vouloir supprimer " + props.student.name + " de la base de données ?"

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutate" :loading="isPending" variant="destructive">
				Supprimer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>