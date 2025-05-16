<script setup lang="ts">

import { WaitingTeams } from "@/components/atoms/illustration"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"

const ACTION_TITLE = "Les équipes ne sont pas encore composées."
const ACTION_DESCRIPTION = "Veuillez patienter jusqu'à ce que les équipes soient composées par le leader du projet."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<WaitingTeams class="w-96" />
	</PlaceholderSection>
</template>