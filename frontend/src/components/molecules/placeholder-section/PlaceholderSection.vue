<script setup lang="ts">

import { Column } from "@/components/atoms/containers"
import InfoText from "@/components/atoms/texts/InfoText.vue"
import Subtitle from "@/components/atoms/texts/Subtitle.vue"

defineProps<{
	title: string
	description: string
}>()

</script>

<template>
	<Column class="w-full h-full items-center justify-center flex-1 gap-1 p-6 border rounded-md bg-white">
		<slot />
		<Subtitle>{{ title }}</Subtitle>
		<InfoText>{{ description }}</InfoText>
		<slot name="action" />
	</Column>
</template>