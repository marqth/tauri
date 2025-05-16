<script setup lang="ts">
import { useQuery } from "@tanstack/vue-query"
import type { Feedback } from "@/types/feedback"
import { getIndividualsCommentsBySprintIdAndTeamId } from "@/services/feedback"
import { ref, watch } from "vue"
import type { User } from "@/types/user"
import type { Student } from "@/types/student"
import { Column, Row } from "@/components/atoms/containers"
import { getStudentsByTeamId } from "@/services/student"
import { Subtitle, Text } from "@/components/atoms/texts"
import CommentsView from "@/components/molecules/rateContainer/CommentsView.vue"
import { MessageCircleReply } from "lucide-vue-next"
import { hasPermission } from "@/services/user"

const props = defineProps<{
  sprintId: string,
  teamId: string,
}>()

const commentsFiltered = ref<Feedback[]>([])
const comments = ref<Feedback[]>([])
const students = ref<Student[]>([])

const { data: com, refetch: refetchFeedbacks } = useQuery<Feedback[], Error>({
	queryKey: ["IndividualComments", props.teamId, props.sprintId],
	queryFn: async() => {
		comments.value = await getIndividualsCommentsBySprintIdAndTeamId(props.sprintId, props.teamId)
		students.value = await getStudentsByTeamId(Number(props.teamId), true)
		return comments.value
	}
})

const getStudentComments = (studentId: number, isFeedback: boolean) => {
	commentsFiltered.value = comments.value.filter(comment => comment.feedback === isFeedback)
	const studentComments = commentsFiltered.value.filter(comment => comment.student && comment.student.id === studentId)
	const authorsStudentComment = studentComments.map(comment => comment.author)
		.filter((author, index, self) => index === self.findIndex((t) => (
			t.id === author.id
		)))
	return { studentComments, authorsStudentComment }
}

watch(() => props.teamId, () => refetchFeedbacks())
watch(() => props.sprintId, () => refetchFeedbacks())

const canViewComments = hasPermission("VIEW_COMMENT")
</script>

<template>
  <Column class="bg-white border rounded-lg mt-4 p-2 w-full">
    <Row class="mb-5">
      <MessageCircleReply class="mr-2" :size="40" :stroke-width="1"/>
      <Subtitle class="mb-4 text-center">Feedbacks Et Commentaire Individuels</Subtitle>
    </Row>
    <div v-for="student in students" :key="student.id" class="mb-3">
      <Subtitle class="mb-5">{{student.name}}</Subtitle>
      <Row class="w-full justify-center">
        <Column class="justify-center w-full">
          <Text>Feedbacks</Text>
          <CommentsView class="border rounded-lg" v-if="getStudentComments(student.id, true).studentComments.length > 0" :is-feedback="true" :comments="getStudentComments(student.id, true).studentComments" :authors="getStudentComments(student.id, true).authorsStudentComment"/>
          <Column v-else class="h-[100px] justify-center items-center border rounded-lg">
            <p>Pas de feedbacks</p>
          </Column>
        </Column>
        <Column v-if="canViewComments" class="ml-3 justify-center w-full">
          <Text>Commentaires</Text>
          <CommentsView class="border rounded-lg" v-if="getStudentComments(student.id, false).studentComments.length > 0" :is-feedback="false" :comments="getStudentComments(student.id, false).studentComments" :authors="getStudentComments(student.id, false).authorsStudentComment"/>
          <Column v-else class="h-[100px] justify-center items-center border rounded-lg">
            <p>Pas de commentaire</p>
          </Column>
        </Column>
      </Row>
    </div>
  </Column>
</template>

<style scoped>

</style>