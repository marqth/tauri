<script setup lang="ts">

import { InfoText, Subtitle } from "@/components/atoms/texts"
import { Column, Row } from "@/components/atoms/containers"

defineProps<{
	title: string,
	infotext : string
}>()
</script>

<template>
	<div class="border rounded-lg p-2 md:p-6">
		<Column class="w-full items-start gap-4">
			<Row class="w-full items-center justify-between h-10">
				<Row class="gap-4 items-center">
					<slot name="icon" />
					<Column>
						<Subtitle>{{title}}</Subtitle>
						<InfoText>{{infotext}}</InfoText>
					</Column>
				</Row>
				<Row class="gap-4">
					<slot name="dialog"/>
				</Row>
			</Row>
		</Column>
	</div>
</template>

<style scoped>

</style>