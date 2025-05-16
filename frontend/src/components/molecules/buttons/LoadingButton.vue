<script setup lang="ts">

import { Loader } from "@/components/atoms/icons"
import { Button, type ButtonVariants } from "@/components/ui/button"

defineProps<{
	variant?: ButtonVariants["variant"]
	size?: ButtonVariants["size"]
	loading?: boolean
	disabled?: boolean
}>()

</script>

<template>
	<Button :variant="variant ?? 'default'" :size="size ?? 'default'" :disabled="disabled || loading || false">
		<Loader class="w-4 h-4 mr-2 animate-spin" v-if="loading ?? false" />
		<slot />
	</Button>
</template>