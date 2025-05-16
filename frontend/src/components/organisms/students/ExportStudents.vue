<script setup lang="ts">

import { ErrorText } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { downloadStudentFile } from "@/services/student"
import { createToast } from "@/utils/toast"
import { useMutation } from "@tanstack/vue-query"
import { ref } from "vue"

const open = ref(false)

const DIALOG_TITLE = "Exporter les étudiants"
const DIALOG_DESCRIPTION = "Vous pouvez exporter le fichier contenant les informations des étudiants ainsi que leur notes au format CSV."

const { isPending, error, mutate: download } = useMutation({ mutationKey: ["export-students"], mutationFn: async() => {
	await downloadStudentFile()
		.then(() => open.value = false)
		.then(() => createToast("Le fichier a été téléchargé."))
} })

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<ErrorText v-if="error">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="download" :loading="isPending">
				Exporter
			</LoadingButton>
		</template>
	</CustomDialog>
</template>