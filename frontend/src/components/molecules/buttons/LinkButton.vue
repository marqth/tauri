<script setup lang="ts">
import { RouterLink } from "vue-router"
import { Button, type ButtonVariants } from "@/components/ui/button"

const props = defineProps<{
	variant?: ButtonVariants["variant"]
	size?: ButtonVariants["size"]
	link?: string | null
	class?: string
}>()

</script>

<template>
	<RouterLink :to="link ?? ''">
		<Button :variant="variant ?? 'default'" :size="size ?? 'default'" :class="props.class">
			<slot />
		</Button>
	</RouterLink>
</template>