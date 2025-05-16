<script setup lang="ts">

import { SidebarTemplate } from "@/components/templates"
import {
	RedirectImportStudents, GenerateTeams, PrepublishDialog, DeleteTeamsDialog, TeamAccordion, TeamsNotCreated,
	PublishDialog, SeeReportsDialog
} from "@/components/organisms/teams"
import { Button } from "@/components/ui/button"
import { getAllStudents } from "@/services/student/student.service"
import { NotAuthorized } from "@/components/organisms/errors"
import { getTeams } from "@/services/team/team.service"
import { getCurrentPhase } from "@/services/project/project.service"
import { Header } from "@/components/molecules/header"
import { computed, onMounted, ref } from "vue"
import { useQuery } from "@tanstack/vue-query"
import SignalTeamDialog from "@/components/organisms/teams/SignalTeamDialog.vue"
import ValidTeamDialog from "@/components/organisms/teams/ValidTeamDialog.vue"
import { userHasValidateTeams } from "@/services/flag/flag.service"
import { hasPermission } from "@/services/user/user.service"
import { Loading } from "@/components/organisms/loading"
import { Cookies } from "@/utils/cookie"
import { StudentSignalTeamDialog } from "@/components/organisms/teams"

const currentUserId = Cookies.getUserId()
const hasValidateTeams = ref(true)

const { data: currentPhase, refetch: refetchCurrentPhase, ...currentPhaseQuery } = useQuery({ queryKey: ["project-phase"], queryFn: getCurrentPhase })
const { data: nbStudents, ...nbStudentsQuery } = useQuery({ queryKey: ["nb-students"], queryFn: async() => (await getAllStudents()).length })
const { data: nbTeams, refetch: refetchTeams, ...nbTeamsQuery } = useQuery({ queryKey: ["nb-teams"], queryFn: async() => (await getTeams()).length })

const handleValidTeams = async() => {
	hasValidateTeams.value = await userHasValidateTeams(currentUserId)
}

onMounted(async() => {
	if (currentUserId) {
		hasValidateTeams.value = await userHasValidateTeams(currentUserId)
	}
})

const authorized = hasPermission("TEAMS_PAGE")
const loading = computed(() => currentPhaseQuery.isLoading.value || nbStudentsQuery.isLoading.value || nbTeamsQuery.isLoading.value)
const canCreate = hasPermission("TEAM_CREATION")
const canPreview = hasPermission("PREVIEW_TEAM")
const displayAdminComposingButtons = computed(() => nbTeams.value && nbTeams.value > 0 && currentPhase.value === "COMPOSING")
const displayAdminPrepublishedButtons = computed(() => currentPhase.value === "PREPUBLISHED")
const displayComposingFlagButtons = computed(() => currentPhase.value === "COMPOSING" && !hasValidateTeams.value)
const displayStudentReportingButton = computed(() => currentPhase.value === "PREPUBLISHED" && hasPermission("ADD_FLAG_TEAM_WITH_STUDENT"))

</script>

<template>
	<SidebarTemplate>
		<Header title="Équipes">
			<DeleteTeamsDialog v-if="canCreate && displayAdminComposingButtons" @delete:teams="refetchTeams">
				<Button variant="outline">Supprimer les équipes</Button>
			</DeleteTeamsDialog>
			<SeeReportsDialog v-if="canCreate && displayAdminComposingButtons">
				<Button variant="outline">Voir les avis</Button>
			</SeeReportsDialog>
			<PrepublishDialog v-if="canCreate && displayAdminComposingButtons" @prepublish:teams="refetchCurrentPhase">
				<Button variant="default">Prépublier</Button>
			</PrepublishDialog>

			<PublishDialog v-if="canCreate && displayAdminPrepublishedButtons" @publish:teams="refetchCurrentPhase">
				<Button variant="default">Publier</Button>
			</PublishDialog>

			<StudentSignalTeamDialog v-if="displayStudentReportingButton">
				<Button variant="outline">Signaler</Button>
			</StudentSignalTeamDialog>

			<SignalTeamDialog v-if="canPreview && nbTeams && nbTeams > 0 && displayComposingFlagButtons">
				<Button variant="outline">Signaler</Button>
			</SignalTeamDialog>
			<ValidTeamDialog v-if="canPreview && nbTeams && nbTeams > 0 && displayComposingFlagButtons"
				@valid:teams="handleValidTeams">
				<Button variant="default">Valider</Button>
			</ValidTeamDialog>
		</Header>

		<NotAuthorized v-if="!authorized" />
		<Loading v-else-if="loading" />
		<RedirectImportStudents v-else-if="canCreate && nbStudents === 0" />
		<GenerateTeams v-else-if="canCreate && nbStudents && nbStudents > 0 && nbTeams === 0"
			@generate:teams="refetchTeams" :nb-students="nbStudents" />
		<TeamAccordion
			v-else-if="canCreate || (canPreview && nbTeams && nbTeams > 0) || currentPhase !== 'COMPOSING'" />
		<TeamsNotCreated v-else-if="!canCreate && currentPhase === 'COMPOSING'" />
		<NotAuthorized v-else />
	</SidebarTemplate>
</template>