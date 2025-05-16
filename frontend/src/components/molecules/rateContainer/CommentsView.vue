<script setup lang="ts">

import type { User } from "@/types/user"
import type { Feedback } from "@/types/feedback"
import { Text } from "@/components/atoms/texts"
import { ScrollArea } from "@/components/ui/scroll-area"

const props = defineProps<{
	isFeedback: boolean,
	authors: User[]
	comments: Feedback[]
}>()

const getCommentsFromAuthor = (authorId: string) => {
	if (props.comments) {
		return props.comments.filter(comment => comment.author.id.toString() === authorId)
	}
}

const placeholderText = props.isFeedback ? "Pas de feedbacks" : "Pas de commentaires"
</script>

<template>
	<div v-if="!comments || comments.length === 0"
		class="h-56 w-full p-4 rounded-lg flex items-center justify-center">
		<Text class="text-center">{{ placeholderText }}</Text>
	</div>
	<div v-else>
		<ScrollArea class="h-56 w-full p-2 rounded-lg">
			<div v-for="author in authors" :key="author.id" class="p-2 flex flex-col">
				<Text class="bold">{{ author.name }}</Text>
				<div v-for="comment in getCommentsFromAuthor(author.id.toString())" :key="comment.id" class="p-2">
					<p class="border rounded-lg p-2 text-gray-500 text-sm break-all">{{ comment.content }}</p>
				</div>
			</div>
		</ScrollArea>
	</div>
</template>

<style scoped></style>