<script setup lang="ts">

import { AddSprintDialog } from "."
import { Button } from "@/components/ui/button"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"
import { WaitingSprints } from "@/components/atoms/illustration"
import type { CalendarDate } from "@internationalized/date"

const emits = defineEmits(["add:sprint"])

defineProps<{
    lastSprintEndDate: CalendarDate | undefined,
	lastSprintOrder: number;
}>()

const ACTION_TITLE = "Vous n'avez pas encore de sprints"
const ACTION_DESCRIPTION = "Pour créer un sprint, il suffit de cliquer sur le bouton."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<WaitingSprints class="h-80" />
		<template #action>
			<AddSprintDialog @add:sprint="emits('add:sprint')" :lastSprintEndDate="lastSprintEndDate"
				:lastSprintOrder="lastSprintOrder">
				<Button class="mt-8">Ajouter un sprint</Button>
			</AddSprintDialog>
		</template>
	</PlaceholderSection>
</template>