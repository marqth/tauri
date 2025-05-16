<script setup lang="ts">

import { ref } from "vue"
import type { CalendarDate, DateValue } from "@internationalized/date"
import { DateFormatter, parseDate } from "@internationalized/date"
import { toDate } from "radix-vue"
import { CalendarCheck } from "lucide-vue-next"
import { Calendar } from "@/components/ui/calendar"
import { Button } from "@/components/ui/button"
import { Popover, PopoverContent, PopoverTrigger } from "@/components/ui/popover"

const props = defineProps<{
	minValue: CalendarDate | undefined,
	maxValue: CalendarDate | undefined,
	actualValue: CalendarDate | undefined,
}>()

const emit = defineEmits(["update:dateValue"])
const open = ref(false)

const onDateSelected = (selectedDate: CalendarDate | undefined) => {
	emit("update:dateValue", selectedDate)
}

const df = new DateFormatter("fr-FR", {
	dateStyle: "long"
})

const placeholder = ref()

const value = ref({
	actual: props.actualValue
})

const onValueChange = (newValue: DateValue | undefined) => {
	if (newValue) {
		onDateSelected(parseDate(newValue.toString()))
	} else {
		onDateSelected(undefined)
	}
	open.value = false
}

</script>

<template>
	<Popover v-model:open="open">
		<PopoverTrigger as-child>
			<Button variant="outline" class="w-[250px] ps-3 text-start font-normal">
				<span>{{ value.actual ? df.format(toDate(value.actual)) : "Choisissez une date" }}</span>
				<CalendarCheck class="ms-auto h-4 w-4 opacity-50" />
			</Button>
			<input hidden>
		</PopoverTrigger>
		<PopoverContent class="w-auto p-0 bg-background">
			<Calendar v-model:placeholder="placeholder" v-model="value.actual" calendar-label="Sprint date"
				initial-focus :min-value="props.minValue" :max-value="props.maxValue" @update:model-value="onValueChange" />
		</PopoverContent>
	</Popover>
</template>