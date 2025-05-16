<script setup lang="ts">

import { WaitingSprintAndTeams } from "@/components/atoms/illustration"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"

const ACTION_TITLE = "Les sprints et les équipes n'ont pas été créés."
const ACTION_DESCRIPTION = "Veuillez attendre que les sprints et les équipes soient créés pour évaluer."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<WaitingSprintAndTeams class="h-80" />
	</PlaceholderSection>
</template>