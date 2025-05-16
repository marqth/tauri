<script setup lang="ts">

import { DialogClose } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { CustomDialog } from "@/components/molecules/dialog"
import { ErrorText, Text } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { Textarea } from "@/components/ui/textarea"
import { ref, computed } from "vue"
import { useMutation, useQueryClient } from "@tanstack/vue-query"
import { createToast } from "@/utils/toast"
import { createComment } from "@/services/feedback"

const props = defineProps<{
	selectedTeamId: string,
	selectedSprintId: string,
	feedback: boolean
}>()

const emits = defineEmits(["add-comment"])
const client = useQueryClient()

const open = ref(false)
const comment = ref("")
const queryType = props.feedback ? "feedbacks" : "comments"

const isDisabled = computed(() => comment.value === "")

const { mutate, isPending, error } = useMutation({
	mutationKey: ["add-comment"], mutationFn: async() => {
		await createComment(props.selectedTeamId, null, comment.value, props.selectedSprintId, props.feedback)
			.then(() => open.value = false)
			.then(() => emits("add-comment"))
			.then(() => client.invalidateQueries({
				queryKey: [queryType, props.selectedTeamId, props.selectedSprintId]
			}))
			.then(() => comment.value = "")
			.then(() => createToast("Le feedback a été enregistré."))
	}
})

const DIALOG_TITLE = props.feedback ? "Donner un feedback" : "Faire un commentaire"
const DIALOG_DESCRIPTION = props.feedback ? "Envoyer un feedback à l'équipe sélectionné sur le déroulement du sprint" : "Faire un commentaire sur l'équipe sélectionné sur le déroulement du sprint"
const textAreaTitle = props.feedback ? "Votre feedback" : "Votre commentaire"
const placeholder = props.feedback ? "Ajouter un feedback" : "Ajouter un commentaire"
</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Text class="-mb-2">{{ textAreaTitle }}</Text>
		<Textarea v-model="comment" :placeholder="placeholder" class="max-h-64"></Textarea>
		<ErrorText v-if="error" class="mt-2">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutate" :loading="isPending" :disabled="isDisabled">
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>

<style scoped></style>