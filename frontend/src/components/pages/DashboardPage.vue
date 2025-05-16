<script setup lang="ts">

import { SidebarTemplate } from "@/components/templates"
import { Cookies } from "@/utils/cookie"
import { Header } from "@/components/molecules/header"
import { formatRole } from "@/types/role"
import { useQuery } from "@tanstack/vue-query"
import { getCurrentUser, hasPermission } from "@/services/user"
import { extractNames } from "@/utils/string"
import { ActionSection } from "@/components/molecules/action-section"
import { RedirectButton } from "@/components/molecules/buttons"
import { Check, GraduationCap, Play, Scale, Tag, User, Users, FileCog } from "lucide-vue-next"
import { Column } from "@/components/atoms/containers"
import { InfoText, Title } from "@/components/atoms/texts"

const role = Cookies.getRole() || "Aucun rôle"
const { data: user } = useQuery({ queryKey: ["current-user"], queryFn: getCurrentUser })

</script>

<template>
	<SidebarTemplate>
		<Header :title="`Bonjour ${user ? extractNames(user?.name).firstName : ''} 👋`" id="welcome-text"/>

		<Column class="border rounded-md p-6 items-center bg-white">
			<Title class="text-dark-blue text-xl">
				Bienvenue sur Tauri !
			</Title>
			<InfoText>
				Votre rôle est : <strong>{{ formatRole(role) }}</strong>.
				Vous pouvez commencer à utiliser l'application en cliquant sur les actions ci-dessous.
			</InfoText>
		</Column>

		<ActionSection
			v-if="hasPermission('STUDENTS_PAGE')"
			title="Gestion des étudiants"
			description="Vous pouvez importer, modifier et exporter les étudiants en vous rendant sur la page “Étudiants”."
		>
			<template #icon>
				<GraduationCap class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/students"> Étudiants </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('TEAMS_PAGE')"
			title="Gestion des équipes"
			description="Vous pouvez générer les équipes en vous rendant sur la page “Équipes”."
		>
			<template #icon>
				<Users class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/teams"> Équipes </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('MY_TEAM_PAGE')"
			title="Mon équipe"
			description="Vous pouvez consulter les informations de votre équipe en vous rendant sur la page “Mon équipe”."
		>
			<template #icon>
				<User class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/my-team"> Mon équipe </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('SPRINTS_PAGE')"
			title="Sprints"
			description="Vous pouvez consulter le découpage du projet en sprints en vous rendant sur la page “Sprints”."
		>
			<template #icon>
				<Play class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/sprints"> Sprints </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('GRADES_PAGE')"
			title="Affichage des notes"
			description="Vous pouvez consulter les notes qui vous concernent en vous rendant sur la page “Notes”."
		>
			<template #icon>
				<Tag class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/grades"> Notes </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('RATING_PAGE')"
			title="Évaluation des étudiants"
			description="Vous pouvez évaluer les étudiants en vous rendant sur la page “Évaluations”."
		>
			<template #icon>
				<Check class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/rating"> Évaluations </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('GRADE_SCALES_PAGE')"
			title="Gestion des barèmes"
			description="Vous pouvez ajouter des barèmes à des types de notes en vous rendant sur la page “Barèmes”."
		>
			<template #icon>
				<Scale class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/grade-scales"> Barèmes </RedirectButton>
		</ActionSection>

		<ActionSection
			v-if="hasPermission('MANAGE_PROJECT')"
			title="Gestion de projet"
			description="Vous pouvez ajouter de nouveaux utilisateurs."
		>
			<template #icon>
				<FileCog class="size-12 stroke-1 text-dark-blue" />
			</template>
			<RedirectButton link="/project"> Gestion de projet </RedirectButton>
		</ActionSection>
	</SidebarTemplate>
</template>