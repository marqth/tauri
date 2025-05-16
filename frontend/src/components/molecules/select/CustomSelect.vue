<script setup lang="ts" generic="T">

import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"

const props = defineProps<{
	class?: string
	placeholder?: string
	data: T[]
	getId:(item: T) => string
	getName:(item: T) => string
}>()

</script>

<template>
	<Select>
		<SelectTrigger :class="props.class">
			<SelectValue :placeholder="placeholder" />
		</SelectTrigger>
		<SelectContent>
			<SelectItem v-for="item in data" :key="getId(item)" :value="getId(item)">
				{{ getName(item) }}
			</SelectItem>
		</SelectContent>
	</Select>
</template>