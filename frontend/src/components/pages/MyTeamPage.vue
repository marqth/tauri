<script setup lang="ts">

import { Cookies } from "@/utils/cookie"
import { computed } from "vue"
import { SidebarTemplate } from "@/components/templates"
import { NotAuthorized } from "@/components/organisms/errors"
import { getCurrentProject } from "@/services/project/project.service"
import { Header } from "@/components/molecules/header"
import { useQuery } from "@tanstack/vue-query"
import { getTeamByUserId } from "@/services/team/team.service"
import { hasPermission } from "@/services/user/user.service"
import { PageSkeleton } from "../atoms/skeletons"
import { TeamsNotCreated } from "../organisms/teams"
import { MyTeamBox } from "../organisms/my-team"

const role = Cookies.getRole()
const userId = Cookies.getUserId()

const { data: currentPhase, isLoading: phaseLoading } = useQuery({
	queryKey: ["project"], queryFn: async() => (await getCurrentProject()).phase
})

const { data: team, isLoading: teamLoading } = useQuery({ queryKey: ["team", userId], queryFn: () => getTeamByUserId(userId) })

const canDisplayTeam = computed(() => role === "SUPERVISING_STAFF" || (role === "OPTION_STUDENT" && currentPhase.value !== "COMPOSING"))

</script>

<template>
	<SidebarTemplate>
		<Header title="Mon équipe" />
		<NotAuthorized v-if="!hasPermission('MY_TEAM_PAGE')" />
		<PageSkeleton v-else-if="phaseLoading || teamLoading" />
		<MyTeamBox v-else-if="canDisplayTeam && team" :team="team"/>
		<TeamsNotCreated v-else />
	</SidebarTemplate>
</template>