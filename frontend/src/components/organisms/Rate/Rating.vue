<script setup lang="ts">

import {
	Users,
	LucideCircleFadingPlus,
	User,
	SquareGanttChart,
	Play,
	Presentation,
	Blocks,
	LucideCirclePlus
} from "lucide-vue-next"
import { hasPermission } from "@/services/user"
import { getTeamByUserId } from "@/services/team"
import { Cookies } from "@/utils/cookie"
import { useQuery } from "@tanstack/vue-query"
import { TeamRatingBox, StudentRatingBox, BonusRatingBox } from "@/components/molecules/rating-box"
import { Column, Row } from "@/components/atoms/containers"
import { getAllRatedGradesFromConnectedUser } from "@/services/grade"
import { Loading } from "../loading"
import CommentsContainer from "@/components/molecules/rateContainer/CommentsContainer.vue"

const props = defineProps<{
	teamId: string,
	sprintId: string
}>()

const currentUserId = Cookies.getUserId()
const { data: currentUserTeam } = useQuery({ queryKey: ["team", currentUserId], queryFn: async() => getTeamByUserId(currentUserId) })
const { data: allGrades } = useQuery({ queryKey: ["all-rated-grades"], queryFn: getAllRatedGradesFromConnectedUser })
const canGradeGlobalPerformance = hasPermission("GRADE_GLOBAL_PERFORMANCE")
const canCommentGlobalPerformance = hasPermission("COMMENT_GLOBAL_PERFORMANCE")
const canGradeLimitedBonus = hasPermission("LIMITED_BONUS_MALUS")
const canGradeUnlimitedBonus = hasPermission("GIVE_UNLIMITED_BONUS_MALUS")
const canGradePresentationContent = hasPermission("GRADE_PRESENTATION_CONTENT")
const canCommentPresentationContent = hasPermission("COMMENT_PRESENTATION_CONTENT")
const canGradeIndividualPerformance = hasPermission("GRADE_INDIVIDUAL_PERFORMANCE")
const canCommentIndividualPerformance = hasPermission("COMMENT_INDIVIDUAL_PERFORMANCE")
const canGradeTechnicalSolution = hasPermission("GRADE_TECHNICAL_SOLUTION")
const canGradeSprintConformity = hasPermission("GRADE_SPRINT_CONFORMITY")
const canGradeProjectManagement = hasPermission("GRADE_PROJECT_MANAGEMENT")
const canCommentTechnicalSolution = hasPermission("COMMENT_TECHNICAL_SOLUTION")
const canCommentSprintConformity = hasPermission("COMMENT_SPRINT_CONFORMITY")
const canCommentProjectManagement = hasPermission("COMMENT_PROJECT_MANAGEMENT")
const canSeeFeedbacks = hasPermission("VIEW_FEEDBACK")
const canSeePrivateComments = hasPermission("VIEW_COMMENT")

</script>

<template>
	<Loading v-if="!allGrades" />

	<Column v-else class="items-stretch flex gap-4 mb-8">
		<Row class="items-stretch justify-start gap-4 flex-1">
			<Column class="gap-4 flex-1">
				<TeamRatingBox
					v-if="(canGradeGlobalPerformance || canCommentGlobalPerformance) && currentUserTeam && currentUserTeam.id.toString() !== props.teamId"
					class="flex-1" :all-grades="allGrades" grade-type-name="Performance globale de l'équipe"
					:sprint-id="props.sprintId" :team-id="props.teamId" :grade-authorization="canGradeGlobalPerformance"
					:comment-authorization="canCommentGlobalPerformance">
					<Users class="size-6 stroke-[1.33] text-dark-blue" />
				</TeamRatingBox>

				<TeamRatingBox
					v-if="(canGradeProjectManagement && currentUserTeam && currentUserTeam.id.toString() === props.teamId) || canCommentProjectManagement"
					class="flex-1" :all-grades="allGrades" grade-type-name="Gestion de projet"
					:sprint-id="props.sprintId" :team-id="props.teamId" :grade-authorization="canGradeProjectManagement"
					:comment-authorization="canCommentProjectManagement">
					<SquareGanttChart class="size-6 stroke-[1.33] text-dark-blue" />
				</TeamRatingBox>

				<TeamRatingBox
					v-if="(canGradeTechnicalSolution && currentUserTeam && currentUserTeam.id.toString() === props.teamId) || canCommentTechnicalSolution"
					class="flex-1" :all-grades="allGrades" grade-type-name="Solution Technique"
					:sprint-id="props.sprintId" :team-id="props.teamId" :grade-authorization="canGradeTechnicalSolution"
					:comment-authorization="canCommentTechnicalSolution">
					<Blocks class="size-6 stroke-[1.33] text-dark-blue" />
				</TeamRatingBox>

				<TeamRatingBox
					v-if="(canGradeSprintConformity && currentUserTeam && currentUserTeam.id.toString() === props.teamId) || canCommentSprintConformity"
					class="flex-1" :all-grades="allGrades" grade-type-name="Conformité au sprint"
					:sprint-id="props.sprintId" :team-id="props.teamId" :grade-authorization="canGradeSprintConformity"
					:comment-authorization="canCommentSprintConformity">
					<Play class="size-6 stroke-[1.33] text-dark-blue" />
				</TeamRatingBox>

				<TeamRatingBox v-if="canGradePresentationContent || canCommentPresentationContent" class="flex-1"
					:all-grades="allGrades" grade-type-name="Contenu de la présentation" :sprint-id="props.sprintId"
					:team-id="props.teamId" :grade-authorization="canGradePresentationContent"
					:comment-authorization="canCommentPresentationContent">
					<Presentation class="size-6 stroke-[1.33] text-dark-blue" />
				</TeamRatingBox>
			</Column>

			<Column class="gap-4 flex-1">
				<CommentsContainer v-if="canSeeFeedbacks" class="flex-1" :isFeedback="true" :sprintId="props.sprintId"
					:teamId="props.teamId" />
				<CommentsContainer v-if="canSeePrivateComments" class="flex-1" :isFeedback="false"
					:sprintId="props.sprintId" :teamId="props.teamId" />
			</Column>
		</Row>

		<Row class="items-stretch justify-start gap-4 flex-1">
			<StudentRatingBox v-if="canGradeIndividualPerformance || canCommentIndividualPerformance" class="flex-1"
				grade-type-name="Performance individuelle" :team-id="props.teamId" :sprint-id="props.sprintId"
				:all-grades="allGrades" :grade-authorization="canGradeIndividualPerformance"
				:comment-authorization="canCommentIndividualPerformance">
				<User class="size-6 stroke-[1.33] text-dark-blue" />
			</StudentRatingBox>

			<BonusRatingBox
				v-if="(canGradeLimitedBonus || canGradeUnlimitedBonus) && currentUserTeam && currentUserTeam.id === Number(props.teamId)"
				class="flex-1" :sprint-id="props.sprintId" :team-id="props.teamId"
				:limited="canGradeLimitedBonus && !canGradeUnlimitedBonus">
				<LucideCirclePlus v-if="canGradeUnlimitedBonus" class="size-6 stroke-[1.33] text-dark-blue" />
				<LucideCircleFadingPlus v-if="canGradeLimitedBonus && !canGradeUnlimitedBonus"
					class="size-6 stroke-[1.33] text-dark-blue" />
			</BonusRatingBox>
		</Row>
	</Column>
</template>