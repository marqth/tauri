<script setup lang="ts">

import { ErrorText } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { updateProject } from "@/services/project/project.service"
import { useMutation } from "@tanstack/vue-query"
import { ref } from "vue"
import { createToast } from "@/utils/toast"

const emits = defineEmits(["publish:teams"])
const open = ref(false)

const { mutate, error, isPending } = useMutation({ mutationKey: ["prepublish-teams"], mutationFn: async() => {
	await updateProject({ phase: "PUBLISHED" })
		.then(() => open.value = false)
		.then(() => emits("publish:teams"))
		.then(() => createToast("La composition des équipes a été publiée."))
} })

const DIALOG_TITLE = "Publier les équipes"
const DIALOG_DESCRIPTION = "Êtes-vous bien sûr de vouloir publier la composition des équipes ?"

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