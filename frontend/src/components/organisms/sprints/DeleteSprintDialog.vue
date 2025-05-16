<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { deleteSprint } from "@/services/sprint/sprint.service"
import LoadingButton from "@/components/molecules/buttons/LoadingButton.vue"
import { ref } from "vue"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { createToast } from "@/utils/toast"

const open = ref(false)
const emits = defineEmits(["delete:sprint"])

const props = defineProps<{
    sprintId: number,
	sprintOrder: number,
}>()

const { mutate, isPending, error } = useMutation({ mutationFn: async() => {
	await deleteSprint(props.sprintId)
		.then(() => open.value = false)
		.then(() => emits("delete:sprint"))
		.then(() => createToast(`Le sprint ${props.sprintOrder} a bien été supprimé.`))
} })

const DIALOG_TITLE = "Supprimer un sprint"
const DIALOG_DESCRIPTION = `Êtes-vous bien sûr de supprimer le sprint ${props.sprintOrder} ?`

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
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>