<script setup lang="ts">

import { MessageSquareReply, MessageCircleMore } from "lucide-vue-next"
import { Button } from "@/components/ui/button"
import { ContainerGradeType } from "@/components/molecules/rateContainer"
import { hasPermission } from "@/services/user/user.service"
import DialogComment from "@/components/organisms/rating/DialogComment.vue"
import DialogViewComment from "@/components/organisms/rating/DialogViewComment.vue"
import DialogViewFeedback from "@/components/organisms/rating/DialogViewFeedback.vue"

const props = defineProps<{
	teamId: string,
	sprintId: string,
	title: string,
	infoText: string,
	feedback: boolean
}>()

const viewButtonTitle = props.feedback ? "Voir les feedbacks" : "Voir les commentaire"
const addButtonTitle = props.feedback ? "Donner un feedback" : "Faire un commentaire"
const canAddComments = props.feedback ? hasPermission("ADD_ALL_TEAMS_FEEDBACK") : hasPermission("ADD_ALL_TEAMS_COMMENT")

</script>

<template>
	<ContainerGradeType :title="props.title" :infotext="props.infoText">
		<template #icon>
			<MessageSquareReply v-if="props.feedback" :size="40" :stroke-width="1" />
			<MessageCircleMore v-else :size="40" :stroke-width="1" />
		</template>
		<template #dialog>
			<DialogViewComment v-if="!props.feedback" :teamId="props.teamId" :sprintId="props.sprintId">
				<Button variant="outline">{{ viewButtonTitle }}</Button>
			</DialogViewComment>
			<DialogViewFeedback v-else :teamId="props.teamId" :sprintId="props.sprintId">
				<Button variant="outline">{{ viewButtonTitle }}</Button>
			</DialogViewFeedback>
			<DialogComment v-if="canAddComments" :selectedTeamId="props.teamId" :selectedSprintId="props.sprintId"
				:feedback="props.feedback">
				<Button variant="default">{{ addButtonTitle }}</Button>
			</DialogComment>
		</template>
	</ContainerGradeType>
</template>

<style scoped></style>