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
import { updateSprint } from "@/services/sprint/sprint.service"
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Info } from "lucide-vue-next"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { Cookies } from "@/utils/cookie"
import { SprintEndTypeSchema, formatSprintEndType, getSprintEndTypeDescription, type Sprint } from "@/types/sprint"
import { dateToCalendarDate, serializeDate } from "@/utils/date"
import { createToast } from "@/utils/toast"
import { Label } from "@/components/ui/label"

const open = ref(false)
const emits = defineEmits(["edit:sprint"])
const currentProjectId = Cookies.getProjectId()

const props = defineProps<{
    sprint: Sprint,
}>()

const startDate = ref<CalendarDate>(dateToCalendarDate(props.sprint.startDate))
const endDate = ref<CalendarDate>(dateToCalendarDate(props.sprint.endDate))
const endType = ref<string>(props.sprint.endType)
const valuesEmpty = ref<boolean>(false)
const sprintOrder = ref<number>(props.sprint.sprintOrder)

const minDate = new CalendarDate(1900, 1, 1)
const maxDate = new CalendarDate(2100, 1, 1)

const { error, isPending, mutate: edit } = useMutation({ mutationFn: async() => {
	if (!startDate.value || !endDate.value || !endType.value || !sprintOrder.value) {
		valuesEmpty.value = true
		return
	}
	valuesEmpty.value = false

	const sprintData = {
		startDate: serializeDate(startDate.value.toDate("UTC")),
		endDate: serializeDate(endDate.value.toDate("UTC")),
		endType: endType.value,
		projectId: currentProjectId ?? "",
		sprintOrder: sprintOrder.value
	}

	await updateSprint(sprintData, props.sprint.id)
		.then(() => open.value = false)
		.then(() => emits("edit:sprint"))
		.then(() => createToast(`Le sprint ${sprintOrder.value} a été modifié avec succès.`))
} })

const DIALOG_TITLE = "Modifier un sprint"
const DIALOG_DESCRIPTION = "Vous pouvez modifier les dates de début et de fin, ainsi que le type de fin sprint."

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
					v-model="startDate" @update:dateValue="(newDate: CalendarDate ) => startDate = newDate"
					:min-value="minDate" :max-value="endDate as CalendarDate" :actual-value="startDate as CalendarDate"
				/>
			</Row>

			<Row class="items-center justify-between my-2">
				<Label>Date de fin</Label>
				<CalendarPopover
					v-model="endDate" @update:dateValue="(newDate: CalendarDate ) => endDate = newDate"
					:min-value="startDate as CalendarDate" :max-value="maxDate" :actual-value="endDate as CalendarDate"
				/>
			</Row>

			<Row class="items-center justify-between my-2">
				<Label class="flex gap-1">Type de sprint
					<TooltipProvider>
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

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue lors de la modification du sprint.</ErrorText>
		<ErrorText v-if="valuesEmpty" class="mb-2">Veuilliez bien remplir tous les champs</ErrorText>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" class="flex items-center" :loading="isPending" @click="edit">
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>