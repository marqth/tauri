<script setup lang="ts">

import { Logo } from "@/components/atoms/logo"
import { Column, Row } from "@/components/atoms/containers"
import Title from "@/components/atoms/texts/Title.vue"
import { Cookies } from "@/utils/cookie"
import { ConnectionForm } from "@/components/organisms/connection"

Cookies.removeAll()

</script>

<template>
	<Row class="w-full h-full items-stretch justify-stretch">
		<img src="/background.png" alt="Background with Tauri Logo" class="w-1/2 h-full object-cover object-top" draggable="false" />

		<Column class="w-1/2 h-full items-center justify-center gap-4 bg-background">
			<Logo class="h-48 fill-dark-blue" />

			<Title class="text-dark-blue text-4xl">Bienvenue sur Tauri !</Title>

			<ConnectionForm />
		</Column>
	</Row>
</template>