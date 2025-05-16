<script setup lang="ts">
import type { Feedback } from "@/types/feedback"
import { ref, watch } from "vue"
import { getCommentsBySprintAndTeam } from "@/services/feedback"
import { Text } from "@/components/atoms/texts"
import { DialogClose } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { CustomDialog } from "@/components/molecules/dialog"
import type { User } from "@/types/user"
import { useQuery } from "@tanstack/vue-query"
import { Input } from "@/components/ui/input"
import { ScrollArea } from "@/components/ui/scroll-area"

const props = defineProps<{
	teamId: string,
	sprintId: string,
}>()
const authorsFeedbacks = ref<User[]>([])
const feedbacksFiltered = ref<Feedback[]>([])

const { data: feedbacks, refetch: refetchFeedbacks } = useQuery<Feedback[], Error>({
	queryKey: ["feedbacks", props.teamId, props.sprintId],
	queryFn: async() => {
		const feedbacks = await getCommentsBySprintAndTeam(props.teamId, props.sprintId)
		feedbacksFiltered.value = feedbacks.filter(feedback => feedback.feedback)
		authorsFeedbacks.value = feedbacksFiltered.value.map(feedback => feedback.author)
			.filter((author, index, self) => index === self.findIndex((t) => (
				t.id === author.id
			)))
		return feedbacks
	}
})

const getFeedbacksFromAuthor = (authorId: string) => {
	if (feedbacksFiltered.value) {
		return feedbacksFiltered.value.filter(feedback => feedback.author.id.toString() === authorId)
	}
}

watch(() => props.teamId, () => refetchFeedbacks())
watch(() => props.sprintId, () => refetchFeedbacks())

const DIALOG_TITLE = "Feedbacks"
const DIALOG_DESCRIPTION = "Feedbacks donnés à l'équipe durant le sprint"
const noFeedbacks = "Aucun feedback donné"

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION">
		<template #trigger>
			<slot />
		</template>
		<div v-if="!feedbacks || feedbacksFiltered.length === 0">
			<Text class="text-center">{{ noFeedbacks }}</Text>
		</div>
		<div v-else>
			<ScrollArea class="h-[500px] w-[450px] p-4">
				<div v-for="author in authorsFeedbacks" :key="author.id" class="p-5 flex flex-col">
					<Text class="bold">{{ author.name }}</Text>
					<div v-for="feedback in getFeedbacksFromAuthor(author.id.toString())" :key="feedback.id"
						class="p-2">
						<Input disabled :default-value="feedback.content" />
					</div>
				</div>
			</ScrollArea>
		</div>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Retour</Button>
			</DialogClose>
		</template>
	</CustomDialog>

</template>

<style scoped></style>