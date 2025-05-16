<script setup lang="ts">

import { ActionSection } from "@/components/molecules/action-section"
import { Button } from "@/components/ui/button"
import { downloadGradeScaleTXT } from "@/services/grade-type"
import type { GradeType } from "@/types/grade-type"
import { createToast } from "@/utils/toast"
import { useMutation } from "@tanstack/vue-query"
import { DeleteGradeTypeDialog } from "."

const emits = defineEmits(["delete:grade-scale"])

const props = defineProps<{
    gradeType: GradeType
}>()

const { mutate: download } = useMutation({
	mutationFn: async() => {
		await downloadGradeScaleTXT(props.gradeType.name)
			.then(() => createToast("Le fichier a été téléchargé."))
	},
	onError: () => createToast("Erreur lors du téléchargement du fichier.")
})

</script>

<template>
    <ActionSection :title="props.gradeType.name" description="">
        <DeleteGradeTypeDialog @delete:grade-scale="emits('delete:grade-scale')" :grade-type="gradeType">
            <Button variant="outline">Supprimer</Button>
        </DeleteGradeTypeDialog>

		<Button variant="default" @click="download">
			Télécharger le barème
		</Button>
    </ActionSection>
</template>