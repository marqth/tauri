<script setup lang="ts">

import { Row, Column } from "@/components/atoms/containers"
import { InfoText, Subtitle } from "@/components/atoms/texts"

defineProps<{
	title: string
	description: string
}>()

</script>

<template>
	<Row class="items-center border rounded-md p-6 gap-4 bg-white">
		<slot name="icon" />

		<Column class="items-start justify-center flex-1 gap-1">
			<Subtitle>{{ title }}</Subtitle>
			<InfoText>{{ description }}</InfoText>
		</Column>

		<slot />
	</Row>
</template>