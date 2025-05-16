<script setup lang="ts">
import { Column, Row } from "@/components/atoms/containers"
import { InfoText, Subtitle } from "@/components/atoms/texts"
import { MessageCircleMore, MessageSquareReply } from "lucide-vue-next"
import { useMutation, useQuery } from "@tanstack/vue-query"
import type { Feedback } from "@/types/feedback"
import { createComment, getCommentsBySprintAndTeam } from "@/services/feedback"
import type { User } from "@/types/user"
import { ref, watch } from "vue"
import CommentsView from "@/components/molecules/rateContainer/CommentsView.vue"
import { Textarea } from "@/components/ui/textarea"
import { SendHorizontal } from "lucide-vue-next"
import { Button } from "@/components/ui/button"
import { createToast } from "@/utils/toast"

const props = defineProps<{
	isFeedback: boolean,
	teamId: string,
	sprintId: string
}>()

const commentsFiltered = ref<Feedback[]>([])
const authorsComments = ref<User[]>([])
const comment = ref("")

const title = props.isFeedback ? "Feedback" : "Commentaire"
const infoText = props.isFeedback ? "Vous pouvez donner un feedback sur les performances de l'équipe durant le sprint" : "Vous pouvez faire des commentaires sur les performances de l'équipe durant le sprint"

const { refetch: refetchFeedbacks } = useQuery<Feedback[], Error>({
	queryKey: ["comments", props.teamId, props.sprintId, props.isFeedback],
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

const { mutate } = useMutation({
	mutationKey: ["add-comment"], mutationFn: async() => {
		if (comment.value.trim() === "") return
		await createComment(props.teamId, null, comment.value, props.sprintId, props.isFeedback)
			.then(() => refetchFeedbacks())
			.then(() => comment.value = "")
			.then(() => createToast(toastText))
	}
})

watch(() => props.teamId, () => refetchFeedbacks())
watch(() => props.sprintId, () => refetchFeedbacks())

const placeholderText = props.isFeedback ? "Ajouter un feedback" : "Ajouter un commentaire"
const toastText = props.isFeedback ? "Le feedback a été enregistré." : "Le commentaire a été enregistré."
</script>

<template>
	<Row class="border rounded-lg p-2 md:p-6 bg-white">
		<Column class="w-1/2 pr-5 flex justify-between">
			<Column class="gap-2">
				<Row class="items-center justify-start gap-2">
					<MessageSquareReply v-if="props.isFeedback" class="size-6 stroke-[1.33] text-dark-blue" />
					<MessageCircleMore v-else class="size-6 stroke-[1.33] text-dark-blue" />
					<Subtitle>{{ title }}</Subtitle>
				</Row>
				<InfoText>{{ infoText }}</InfoText>
			</Column>
			<Row class="">
				<Textarea v-model="comment" :placeholder="placeholderText"
					class="w-full mr-2 resize-none min-h-[10px] max-h-[40px] overflow-auto " />
				<Button variant="default" size="icon" class="rounded-full h-10 w-14" @click="mutate">
					<SendHorizontal class="w-5 h-5" />
				</Button>
			</Row>
		</Column>
		<div class="w-1/2">
			<div class="bg-background">
				<CommentsView class="bg-gray-50" :authors="authorsComments" :comments="commentsFiltered"
					:isFeedback="props.isFeedback" />
			</div>
		</div>
	</Row>
</template>