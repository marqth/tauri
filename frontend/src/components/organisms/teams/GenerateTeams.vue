<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { ActionSection } from "@/components/molecules/action-section"
import GenerateTeamsDialog from "./GenerateTeamsDialog.vue"

const emits = defineEmits(["generate:teams"])

defineProps<{
	nbStudents: number
}>()

const ACTION_TITLE = "Vous n'avez pas encore d'équipes"
const ACTION_DESCRIPTION = "Maintenant que les étudiants sont importés, il vous suffit de générer les équipes automatiquement."

</script>

<template>
	<ActionSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<GenerateTeamsDialog @generate:teams="emits('generate:teams')" :nb-students="nbStudents">
			<Button>Générer les équipes</Button>
		</GenerateTeamsDialog>
	</ActionSection>
</template>