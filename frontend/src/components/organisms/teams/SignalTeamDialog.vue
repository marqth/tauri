<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ref } from "vue"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText, Text } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { Textarea } from "@/components/ui/textarea"
import { createReportingFlag } from "@/services/flag"
import { createToast } from "@/utils/toast"

const open = ref(false)
const description = ref("")

const { mutate, isPending, error } = useMutation({ mutationKey: ["signal-teams"], mutationFn: async() => {
	if (!description.value) return
	await createReportingFlag(description.value)
		.then(() => open.value = false)
		.then(() => description.value = "")
		.then(() => createToast("Le signalement a été envoyé."))
} })

const DIALOG_TITLE = "Signaler la composition des équipes"
const DIALOG_DESCRIPTION = "Envoyez un signalement sur la composition des équipes au project leader."

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Text class="-mb-2">Votre commentaire</Text>
		<Textarea v-model="description" placeholder="Ajouter un commentaire" class="max-h-64"></Textarea>
		<ErrorText v-if="error" class="mt-2">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutate" :loading="isPending" :disabled="description === ''">
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>