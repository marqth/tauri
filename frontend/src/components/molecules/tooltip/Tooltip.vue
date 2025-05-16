<script setup lang="ts">
import { defineProps } from "vue"
import { VueLatex } from "vatex"


const props = defineProps<{
	expression: string;
	position : string;
}>()

</script>

<template>
	<div class="relative group">
		<slot></slot>
		<div :class="props.position" class="absolute top-full mt-2 w-max px-3 py-2 bg-gray-700 text-white text-xs rounded-lg opacity-0 group-hover:opacity-100 transition-opacity overflow-x-auto break-words">
			<VueLatex :expression="props.expression" display-mode />
		</div>
	</div>
</template>

<style scoped>
.group:hover .group-hover\:opacity-100 {
	opacity: 1;
}
</style>