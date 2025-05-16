<script setup lang="ts">

import { EditSprintDialog, DeleteSprintDialog, PresentationOrderBox } from "."
import { formatSprintEndTypeWithDescription, type Sprint } from "@/types/sprint"
import { Button } from "@/components/ui/button"
import { formatDate } from "@/utils/date"
import { Cookies } from "@/utils/cookie"
import { Column, Row } from "@/components/atoms/containers"
import { InfoText, Subtitle } from "@/components/atoms/texts"
import { useQuery } from "@tanstack/vue-query"
import { getCurrentPhase } from "@/services/project"
import { computed } from "vue"

const role = Cookies.getRole()
const emits = defineEmits(["edit:sprint", "delete:sprint"])

const props = defineProps<{
	sprint: Sprint,
	canEditSprints: boolean
}>()

const { data: currentPhase } = useQuery({ queryKey: ["current-phase"], queryFn: getCurrentPhase })

const TITLE = `Sprint ${props.sprint.sprintOrder} : du ${formatDate(props.sprint.startDate)} au ${formatDate(props.sprint.endDate)}`
const DESCRIPTION = formatSprintEndTypeWithDescription(props.sprint.endType)

const displayPresentationOrder = computed(
	() => props.sprint.endType !== "UNGRADED_SPRINT" && currentPhase.value === "PUBLISHED" && (role === "TEAM_MEMBER" || role === "OPTION_STUDENT")
)

</script>

<template>
	<Column class="border rounded-md p-6 gap-4 bg-white">
		<Row class="items-center gap-4">
			<Column class="items-start justify-center flex-1 gap-1">
				<Subtitle>{{ TITLE }}</Subtitle>
				<InfoText>{{ DESCRIPTION }}</InfoText>
			</Column>

			<EditSprintDialog v-if="canEditSprints" @edit:sprint="emits('edit:sprint')" :sprint="sprint">
				<Button variant="outline">Modifier</Button>
			</EditSprintDialog>

			<DeleteSprintDialog v-if="canEditSprints" @delete:sprint="emits('delete:sprint')" :sprintId="sprint.id"
				:sprintOrder="sprint.sprintOrder">
				<Button variant="outline">Supprimer</Button>
			</DeleteSprintDialog>
		</Row>
		<PresentationOrderBox v-if="displayPresentationOrder" :sprint="sprint" />
	</Column>

</template>