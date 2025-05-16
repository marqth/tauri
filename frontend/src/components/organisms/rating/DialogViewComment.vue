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
const authorsComments = ref<User[]>([])
const commentsFiltered = ref<Feedback[]>([])

const { data: comments, refetch: refetchFeedbacks } = useQuery<Feedback[], Error>({
	queryKey: ["comments", props.teamId, props.sprintId],
	queryFn: async() => {
		const comments = await getCommentsBySprintAndTeam(props.teamId, props.sprintId)
		commentsFiltered.value = comments.filter(comment => !comment.feedback)
		authorsComments.value = commentsFiltered.value.map(comment => comment.author)
			.filter((author, index, self) => index === self.findIndex((t) => (
				t.id === author.id
			)))
		return comments
	}
})

const getCommentsFromAuthor = (authorId: string) => {
	if (commentsFiltered.value) {
		return commentsFiltered.value.filter(comment => comment.author.id.toString() === authorId)
	}
}

watch(() => props.teamId, () => refetchFeedbacks())
watch(() => props.sprintId, () => refetchFeedbacks())

const DIALOG_TITLE = "Commentaires"
const DIALOG_DESCRIPTION = "Commentaires donnés à l'équipe durant le sprint"
const noComments = "Aucun commentaire donné"

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION">
		<template #trigger>
			<slot />
		</template>
		<div v-if="!comments || commentsFiltered.length === 0">
			<Text class="text-center">{{ noComments }}</Text>
		</div>
		<div v-else>
			<ScrollArea class="h-[500px] w-[450px] p-4">
				<div v-for="author in authorsComments" :key="author.id" class="p-5 flex flex-col">
					<Text class="bold">{{ author.name }}</Text>
					<div v-for="comment in getCommentsFromAuthor(author.id.toString())" :key="comment.id" class="p-2">
						<Input disabled :default-value="comment.content" />
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