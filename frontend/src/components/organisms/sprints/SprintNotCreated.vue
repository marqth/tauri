<script setup lang="ts">

import { WaitingSprints } from "@/components/atoms/illustration"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"

const ACTION_TITLE = "Les sprints ne sont pas encore créés."
const ACTION_DESCRIPTION = "Veuillez patienter jusqu'à ce que les sprints soient créés par le leader du projet."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<WaitingSprints class="h-96" />
	</PlaceholderSection>
</template>