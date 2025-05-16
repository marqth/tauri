<script setup lang="ts">

import { computed } from "vue"
import { cn } from "@/utils/style"
import { useRoute } from "vue-router"
import { LinkButton } from "@/components/molecules/buttons"
import type { PermissionType } from "@/types/permission"
import { hasPermission } from "@/services/user"

const route = useRoute()

const props = defineProps<{
	link?: string | null
	permission?: PermissionType
	class?: string
}>()

const selected = computed(() => props.link && route.path === props.link)

const style = cn(
	"w-full my-1",
	"flex flex-row items-center justify-start gap-2",
	"text-white transition-colors hover:text-white",
	{ "bg-white/20 hover:bg-white/20": selected.value },
	{ "hover:bg-white/10": !selected.value },
	props.class
)

</script>

<template>
	<LinkButton :link="link" :class="style" variant="ghost" v-if="!permission || hasPermission(permission)">
		<slot />
	</LinkButton>
</template>