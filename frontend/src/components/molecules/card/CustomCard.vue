<script setup lang="ts">

import { Card, CardHeader, CardTitle, CardContent, CardFooter, CardDescription } from "@/components/ui/card"

defineProps<{
	title: string
	description: string
}>()

</script>

<template>
	<Card>
		<CardHeader>
			<CardTitle>{{ title }}</CardTitle>
			<CardDescription>{{ description }}</CardDescription>
		</CardHeader>

		<CardContent>
			<slot />
		</CardContent>

		<CardFooter class="flex justify-end">
			<slot name="footer" />
		</CardFooter>
	</Card>
</template>