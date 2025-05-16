<script setup lang="ts">

import { Row } from "@/components/atoms/containers"
import RadioGroupItem from "@/components/ui/radio-group/RadioGroupItem.vue"
import Label from "@/components/ui/label/Label.vue"

defineProps<{
	name: string
	value: string
}>()

</script>

<template>
	<Row class="items-center gap-2">
		<RadioGroupItem :name="name" :id="value" :value="value" />
		<Label :for="value" class="cursor-pointer">
			<slot />
		</Label>
	</Row>
</template>