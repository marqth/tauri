<script setup lang="ts">

import { AddGradeScaleDialog } from "."
import { Button } from "@/components/ui/button"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"
import { NoGradeScale } from "@/components/atoms/illustration"

const emits = defineEmits(["add:grade-scale"])

const ACTION_TITLE = "Vous n'avez pas encore de barèmes"
const ACTION_DESCRIPTION = "Pour importer un barème, il suffit de cliquer sur le bouton."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<NoGradeScale class="h-80" />
		<template #action>
			<AddGradeScaleDialog @add:grade-scale="emits('add:grade-scale')">
				<Button class="mt-8">Importer un barème</Button>
			</AddGradeScaleDialog>
		</template>
	</PlaceholderSection>
</template>