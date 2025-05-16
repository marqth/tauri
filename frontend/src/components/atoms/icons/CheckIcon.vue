<script setup lang="ts">

import { cn } from "@/utils/style"
import { Check, XIcon } from "lucide-vue-next"

const props = defineProps<{
	checked?: boolean | null
}>()

const style = cn(
	"w-4 h-4 fill-none"
)

</script>

<template>
	<div v-if="props.checked === null"></div>
	<Check v-else-if="props.checked" class="stroke-green-700" :class="style" />
	<XIcon v-else class="stroke-red-700" :class="style" />
</template>