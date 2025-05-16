<script setup lang="ts">

import { Button } from "@/components/ui/button"
import LoadingButton from "@/components/molecules/buttons/LoadingButton.vue"
import { ref } from "vue"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { createToast } from "@/utils/toast"
import type { GradeType } from "@/types/grade-type"
import { deleteGradeScaleTXT } from "@/services/grade-type"

const open = ref(false)
const emits = defineEmits(["delete:grade-scale"])

const props = defineProps<{
    gradeType: GradeType
}>()

const { mutate, isPending, error } = useMutation({ mutationFn: async() => {
	await deleteGradeScaleTXT(props.gradeType.id.toString())
		.then(() => open.value = false)
		.then(() => emits("delete:grade-scale"))
		.then(() => createToast("Le barème a bien été supprimé."))
} })

const DIALOG_TITLE = "Supprimer un barème"
const DIALOG_DESCRIPTION = `Êtes-vous bien sûr de supprimer le barème pour la note ${props.gradeType.name} ?`

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
			<LoadingButton type="submit" @click="mutate" :loading="isPending">
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>