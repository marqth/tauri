<script setup lang="ts">

import { ref } from "vue"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ErrorText } from "@/components/atoms/texts"
import { Button } from "@/components/ui/button"
import CalendarPopover from "@/components/molecules/calendar/CalendarPopover.vue"
import { Column, Row } from "@/components/atoms/containers"
import { CalendarDate } from "@internationalized/date"
import { useMutation } from "@tanstack/vue-query"
import LoadingButton from "@/components/molecules/buttons/LoadingButton.vue"
import { addSprint } from "@/services/sprint/sprint.service"
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Info } from "lucide-vue-next"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { Cookies } from "@/utils/cookie"
import { serializeCalendarDate } from "@/utils/date"
import { createToast } from "@/utils/toast"
import { SprintEndTypeSchema, formatSprintEndType, getSprintEndTypeDescription } from "@/types/sprint"
import { Label } from "@/components/ui/label"

const open = ref(false)
const emits = defineEmits(["add:sprint"])
const currentProjectId = Cookies.getProjectId()

const props = defineProps<{
	lastSprintEndDate: CalendarDate | undefined,
	lastSprintOrder: number;
}>()

const startDate = ref<CalendarDate>()
const endDate = ref<CalendarDate>()
const endType = ref<string>()
const sprintOrder = ref<number>(props.lastSprintOrder + 1)
const valuesEmpty = ref<boolean>(false)

const minDate = new CalendarDate(1900, 1, 1)
const maxDate = new CalendarDate(2100, 1, 1)

const nextDayAfterStartDate = (date: CalendarDate | undefined) => {
	if (date === undefined) return
	return new CalendarDate(date.year, date.month, date.day + 1)
}

const { error, isPending, mutate: add } = useMutation({ mutationFn: async() => {
	if (!startDate.value || !endDate.value || !endType.value || !sprintOrder.value) {
		valuesEmpty.value = true
		return
	}
	valuesEmpty.value = false

	const sprintData = {
		startDate: serializeCalendarDate(startDate.value),
		endDate: serializeCalendarDate(endDate.value),
		endType: endType.value,
		projectId: currentProjectId ?? "",
		sprintOrder: sprintOrder.value
	}

	await addSprint(sprintData)
		.then(() => open.value = false)
		.then(() => emits("add:sprint"))
		.then(() => createToast(`Le sprint ${sprintOrder.value} a bien été ajouté.`))
}
})

const DIALOG_TITLE = "Ajouter un sprint"
const DIALOG_DESCRIPTION = "Pour ajouter un sprint, vous devez spécifier les dates de début et de fin, ainsi que le type de fin de sprint."

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Column>
			<Row class="items-center justify-between my-2">
				<Label>Date de début</Label>
				<CalendarPopover
					v-model="startDate" @update:dateValue="(newDate: CalendarDate) => startDate = newDate"
					:min-value="lastSprintEndDate != undefined ? nextDayAfterStartDate(lastSprintEndDate) : minDate" :max-value="endDate"
					:actual-value="undefined"
				/>
			</Row>

			<Row class="items-center justify-between my-2">
				<Label>Date de fin</Label>
				<CalendarPopover
					v-model="endDate" @update:dateValue="(newDate: CalendarDate) => endDate = newDate"
					:min-value="nextDayAfterStartDate(startDate)" :max-value="maxDate"
					:actual-value="undefined"
				/>
			</Row>

			<!-- <Row class="items-center justify-between my-2">
				<Label>Ordre de ce sprint</Label>
				<Input v-model="sprintOrder" type="number" min="1" class="w-[250px]" />
			</Row> -->

			<Row class="items-center justify-between my-2">
				<Label class="flex gap-1">Type de fin de sprint
					<TooltipProvider :delay-duration="200">
						<Tooltip>
							<TooltipTrigger>
								<Info class="size-4" />
							</TooltipTrigger>
							<TooltipContent>
								<p v-for="sprintEndType in SprintEndTypeSchema.options" :key="sprintEndType">
									<strong>{{ formatSprintEndType(sprintEndType) }} :</strong>
									{{ getSprintEndTypeDescription(sprintEndType) }}
								</p>
							</TooltipContent>
						</Tooltip>
					</TooltipProvider>
				</Label>

				<Select v-model="endType">
					<SelectTrigger class="w-[250px]">
						<SelectValue placeholder="Type de fin de sprint" />
					</SelectTrigger>
					<SelectContent>
						<SelectGroup>
							<SelectItem v-for="sprintEndType in SprintEndTypeSchema.options" :key="sprintEndType" :value="sprintEndType">
								{{ formatSprintEndType(sprintEndType) }}
							</SelectItem>
						</SelectGroup>
					</SelectContent>
				</Select>
			</Row>
		</Column>

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue lors de l'ajout du sprint.</ErrorText>
		<ErrorText v-if="valuesEmpty" class="mb-2">Veuilliez bien remplir tous les champs.</ErrorText>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" class="flex items-center" :loading="isPending" @click="add">
				Continuer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>