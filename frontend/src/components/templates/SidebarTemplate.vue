<script setup lang="ts">

import { computed } from "vue"
import { Sidebar } from "@/components/organisms/sidebar"
import { cn } from "@/utils/style"

const props = defineProps<{
	class?: string
}>()

const style = computed(() => {
	return cn(
		"h-screen w-screen",
		"flex flex-row items-stretch justify-start",
		"overflow-hidden",
		props.class
	)
})

</script>

<template>
	<section :class="style">
		<Sidebar />

		<div class="flex-1 flex flex-col items-stretch justify-start gap-4 p-8 overflow-scroll">
			<slot />
		</div>
	</section>
</template>