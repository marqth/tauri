<script setup lang="ts">
import { RouterLink } from "vue-router"
import { Button, type ButtonVariants } from "@/components/ui/button"
import { ExternalLink } from "lucide-vue-next"

const props = defineProps<{
	variant?: ButtonVariants["variant"]
	size?: ButtonVariants["size"]
	link?: string | null
	class?: string
}>()

</script>

<template>
	<RouterLink :to="link ?? ''">
		<Button :variant="variant ?? 'default'" :size="size ?? 'default'" class="flex items-center gap-2" :class="props.class" >
			<ExternalLink class="size-4" />
			<slot />
		</Button>
	</RouterLink>
</template>