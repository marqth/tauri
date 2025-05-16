<script setup lang="ts">

import { SidebarTemplate } from "@/components/templates"
import { ref, watch } from "vue"
import { hasPermission } from "@/services/user"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { Header } from "@/components/molecules/header"
import { Column } from "@/components/atoms/containers"
import Grade from "@/components/organisms/Grade/GradeTable.vue"
import { Button } from "@/components/ui/button"
import ValidGradesDialog from "@/components/organisms/Grade/ValidGradesDialog.vue"
import { getGradesConfirmation } from "@/services/grade"
import ExportGrades from "../organisms/Grade/ExportGrades.vue"
import { SprintSelect, TeamSelect } from "../molecules/select"
import { Cookies } from "@/utils/cookie"
import { getTeamByUserId } from "@/services/team"
import { TeamSelect2 } from "@/components/molecules/select"
import { setValidationBonusesByTeam } from "@/services/bonus/bonus.service"
import { LoadingButton } from "../molecules/buttons"
import { createToast } from "@/utils/toast"
import { NotAuthorized } from "@/components/organisms/errors"
import GradeNotSelected from "../organisms/Grade/GradeNotSelected.vue"
import SprintsAndTeamsNotCreated from "@/components/organisms/Grade/SprintsAndTeamsNotCreated.vue"
import { getCurrentPhase } from "@/services/project"

const teamId = ref<string | null>(null)
const sprintId = ref<string | null>(null)

const authorized = hasPermission("GRADES_PAGE")
const canConfirmOwnTeamGrade = hasPermission("GRADE_CONFIRMATION")
const { data: currentPhase } = useQuery({ queryKey: ["project-phase"], queryFn: getCurrentPhase })
let isGradesConfirmed = ref(false)

const { data: actualTeam } = useQuery({ queryKey: ["team", Cookies.getUserId()], queryFn: () => getTeamByUserId(Cookies.getUserId()) })

const { refetch: refetchGradesConfirmation } = useQuery({
	queryKey: ["grades-confirmation", sprintId.value, teamId.value],
	queryFn: async() => {
		isGradesConfirmed.value = await getGradesConfirmation(parseInt(sprintId.value), parseInt(teamId.value))
	}
})


const { mutate: studentValidLimitedBonus, isPending: studentBtnLoading } = useMutation({
	mutationKey: ["student-valid-bonus", sprintId.value, teamId.value], mutationFn: async() => {
		if (sprintId.value === null || teamId.value === null || actualTeam.value?.id == undefined) return false
		await setValidationBonusesByTeam(parseInt(teamId.value), parseInt(sprintId.value), Cookies.getUserId())
			.then(() => createToast("Les bonus limités ont été validés avec succès"))
			.catch(() => createToast("Erreur lors de la validation des bonus limités"))
	}
})


const canViewAllOg = hasPermission("VIEW_ALL_ORAL_GRADES")
const canViewAllWg = hasPermission("VIEW_ALL_WRITING_GRADES")

watch(() => [teamId, sprintId], () => {
	refetchGradesConfirmation()
})

</script>

<template>
	<SidebarTemplate>
		<NotAuthorized v-if="!authorized" />
		<Column v-else class="gap-4 h-full">
			<Header title="Notes">
				<ValidGradesDialog
					v-if="teamId !== null && sprintId !== null && canConfirmOwnTeamGrade && actualTeam?.id.toString() == teamId"
					@valid:individual-grades="refetchGradesConfirmation" :selectedTeam="teamId"
					:selectedSprint="sprintId">
					<Button variant="outline">Validation des notes</Button>
				</ValidGradesDialog>
				<LoadingButton :variant="'outline'" v-if="Cookies.getRole() == 'OPTION_STUDENT' && actualTeam?.id.toString() == teamId"
							   type="submit" @click="studentValidLimitedBonus" :loading="studentBtnLoading">
					Valider les bonus limités
				</LoadingButton>
				<SprintSelect v-model="sprintId" />
				<TeamSelect v-model="teamId" v-if="canViewAllWg || canViewAllOg" />
				<TeamSelect2 v-model="teamId" v-else />

				<ExportGrades v-if="hasPermission('EXPORT_INDIVIDUAL_GRADES')">
					<Button variant="default">Exporter</Button>
				</ExportGrades>
			</Header>
			<Column v-if="teamId !== null && sprintId !== null">
				<Grade v-if="authorized" :teamId="teamId ?? ''" :sprintId="sprintId ?? ''"
					   :is-grades-confirmed="isGradesConfirmed ?? false" />
			</Column>
			<SprintsAndTeamsNotCreated v-else-if="currentPhase === 'COMPOSING' || currentPhase === 'PREPUBLISHED'"/>
			<GradeNotSelected v-else />
		</Column>
	</SidebarTemplate>
</template>