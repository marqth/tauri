<script setup lang="ts">
import { useQuery } from "@tanstack/vue-query"
import type { Feedback } from "@/types/feedback"
import { getIndividualsCommentsBySprintIdAndTeamId } from "@/services/feedback"
import { Cookies } from "@/utils/cookie"
import { ref, watch } from "vue"
import type { User } from "@/types/user"
import { Column, Row } from "@/components/atoms/containers"
import { MessageCircleReply } from "lucide-vue-next"
import { Subtitle, Text } from "@/components/atoms/texts"
import CommentsView from "@/components/molecules/rateContainer/CommentsView.vue"

const props = defineProps<{
  sprintId: string,
  teamId: string,
}>()

const currentUserId = Cookies.getUserId()
const authors = ref<User[]>([])

const { data: studentComments, refetch: refetchFeedbacks } = useQuery<Feedback[], Error>({
	queryKey: ["IndividualComment", props.teamId, props.sprintId],
	queryFn: async() => {
		const comments = await getIndividualsCommentsBySprintIdAndTeamId(props.sprintId, props.teamId)
		console.log(comments)
		console.log(currentUserId)
		const studentComments = comments.filter(comment => comment.student && (comment.student.id.toString() === currentUserId.toString()) && comment.feedback)
		console.log(studentComments)
		authors.value = studentComments.map(comment => comment.author)
			.filter((author, index, self) => index === self.findIndex((t) => (
				t.id === author.id
			)))
		return studentComments
	}
})

watch(() => props.teamId, () => refetchFeedbacks())
watch(() => props.sprintId, () => refetchFeedbacks())

</script>

<template>
  <Column  v-if="studentComments && studentComments.length > 0" class="bg-white border rounded-lg mt-4 p-2 w-full">
    <Row class="mb-5">
      <MessageCircleReply class="mr-2" :size="40" :stroke-width="1"/>
      <Subtitle class="mb-4 text-center">Feedbacks Individuel</Subtitle>
    </Row>
      <Row class="w-full justify-center">
        <Column class="justify-center w-full">
          <Text>Feedbacks</Text>
          <CommentsView class="border rounded-lg" :is-feedback="true" :comments="studentComments" :authors="authors"/>
        </Column>
      </Row>
  </Column>
</template>

<style scoped>

</style>