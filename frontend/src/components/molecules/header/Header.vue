<script setup lang="ts">

import { Title } from "@/components/atoms/texts"
import { Separator } from "@/components/ui/separator"
import { Column, Row } from "@/components/atoms/containers"

defineProps<{
	title: string
}>()

</script>

<template>
	<Column class="w-full items-start gap-4">
		<Row class="w-full items-center justify-between h-10">
			<Title>{{ title }}</Title>
			<Row class="gap-4">
				<slot />
			</Row>
		</Row>
		<Separator />
	</Column>
</template>