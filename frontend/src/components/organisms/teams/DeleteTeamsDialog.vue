<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { deleteAllTeams } from "@/services/team"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ref } from "vue"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { createToast } from "@/utils/toast"

const emits = defineEmits(["delete:teams"])
const open = ref(false)

const { mutate, isPending, error } = useMutation({ mutationKey: ["delete-teams"], mutationFn: async() => {
	await deleteAllTeams()
		.then(() => open.value = false)
		.then(() => emits("delete:teams"))
		.then(() => createToast("Les équipes ont été supprimées."))
} })

const DIALOG_TITLE = "Supprimer les équipes"
const DIALOG_DESCRIPTION = "Êtes-vous bien sûr de vouloir supprimer toutes les équipes ?"

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