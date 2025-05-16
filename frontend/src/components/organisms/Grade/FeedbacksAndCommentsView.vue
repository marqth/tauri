<script setup lang="ts">
import type { Feedback } from "@/types/feedback"
import { ref, watch } from "vue"
import { getCommentsBySprintAndTeam } from "@/services/feedback"
import { Subtitle, Text } from "@/components/atoms/texts"
import type { User } from "@/types/user"
import { useQuery } from "@tanstack/vue-query"
import CommentsView from "@/components/molecules/rateContainer/CommentsView.vue"
import { Column, Row } from "@/components/atoms/containers"
import { MessageCircleMore, MessageSquareReply } from "lucide-vue-next"

const props = defineProps<{
	teamId: string,
	sprintId: string,
	isFeedback: boolean
}>()

const authorsComments = ref<User[]>([])
const commentsFiltered = ref<Feedback[]>([])
const typeComments = props.isFeedback ? "feedbacks" : "comments"

const { data: comments, refetch: refetchFeedbacks } = useQuery<Feedback[], Error>({
	queryKey: [typeComments, props.teamId, props.sprintId],
	queryFn: async() => {
		const comments = await getCommentsBySprintAndTeam(props.teamId, props.sprintId)
		commentsFiltered.value = comments.filter(comment => comment.feedback === props.isFeedback)
		authorsComments.value = commentsFiltered.value.map(comment => comment.author)
			.filter((author, index, self) => index === self.findIndex((t) => (
				t.id === author.id
			)))
		return comments
	}
})

watch(() => props.teamId, () => refetchFeedbacks())
watch(() => props.sprintId, () => refetchFeedbacks())

const TITLE = props.isFeedback ? "Feedbacks" : "Commentaires"
const noComments = props.isFeedback ? "Aucun feedbacks donné" : "Aucun commentaire donné"

</script>

<template>
	<Column class="pr-5 h-full flex justify-start border bg-white rounded-md mt-2 w-full">
		<Row class="p-5 gap-2">
			<MessageSquareReply v-if="props.isFeedback" class="size-6 stroke-[1.33] text-dark-blue" />
			<MessageCircleMore v-else class="size-6 stroke-[1.33] text-dark-blue" />
			<Subtitle class="text-center">{{ TITLE }}</Subtitle>
		</Row>
		<div v-if="!comments || commentsFiltered.length === 0" class="h-[100px] items-center">
			<Text class="text-center">{{ noComments }}</Text>
		</div>
		<div v-else>
			<CommentsView class="bg-white" :authors="authorsComments" :comments="commentsFiltered"
				:isFeedback="props.isFeedback" />
		</div>
	</Column>
</template>