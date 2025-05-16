<script setup lang="ts">

import { NotSelected } from "@/components/atoms/illustration"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"

const ACTION_TITLE = "Vous n'avez pas sélectionné de sprint et/ou une équipe à évaluer."
const ACTION_DESCRIPTION = "Veuillez sélectionner un sprint et une équipe pour continuer."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<NotSelected class="h-80" />
	</PlaceholderSection>
</template>