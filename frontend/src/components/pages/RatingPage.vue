<script setup lang="ts">

import { ref } from "vue"
import { SidebarTemplate } from "@/components/templates"
import { Header } from "@/components/molecules/header"
import { Column } from "@/components/atoms/containers"
import { hasPermission } from "@/services/user/user.service"
import { NotAuthorized } from "@/components/organisms/errors"
import Rating from "@/components/organisms/Rate/Rating.vue"
import { SprintSelect, TeamSelect } from "../molecules/select"
import GradeNotSelected from "../organisms/Grade/GradeNotSelected.vue"
import { useQuery } from "@tanstack/vue-query"
import { getCurrentPhase } from "@/services/project"
import SprintsAndTeamsNotCreated from "@/components/organisms/Grade/SprintsAndTeamsNotCreated.vue"

const teamId = ref<string | null>(null)
const sprintId = ref<string | null>(null)

const { data: currentPhase } = useQuery({ queryKey: ["project-phase"], queryFn: getCurrentPhase })

</script>

<template>
	<SidebarTemplate>
		<NotAuthorized v-if="!hasPermission('RATING_PAGE')" />
		<Column v-else class="gap-4 h-full">
			<Header title="Évaluations">
				<SprintSelect v-model="sprintId" v-if="currentPhase ===  'PUBLISHED'"/>
				<TeamSelect v-model="teamId" v-if="currentPhase ===  'PUBLISHED'"/>
			</Header>

			<Column v-if="teamId !== null && sprintId !== null && currentPhase ===  'PUBLISHED' " class="gap-4">
				<Rating :teamId="teamId" :sprintId="sprintId" />
			</Column>

			<SprintsAndTeamsNotCreated v-else-if="currentPhase === 'COMPOSING' || currentPhase === 'PREPUBLISHED'" />
            <GradeNotSelected v-else />
		</Column>
	</SidebarTemplate>
</template>