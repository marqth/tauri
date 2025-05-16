<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { RouterLink } from "vue-router"
import { Logo } from "@/components/atoms/logo"
import { Text, Title } from "@/components/atoms/texts"
import { Column } from "@/components/atoms/containers"

const props = defineProps<{
	title: string
	description: string
	logoClass?: string
	class?: string
}>()

</script>

<template>
	<Column class="w-full h-screen items-center justify-center gap-6" :class="props.class">
		<Logo class="h-64" :class="props.logoClass" />

		<Column class="items-center justify-center">
			<Title>{{ title }}</Title>
			<Text>{{ description }}</Text>
		</Column>

		<RouterLink to="/">
			<Button>Retour à l'accueil</Button>
		</RouterLink>
	</Column>
</template>