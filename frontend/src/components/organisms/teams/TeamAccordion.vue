<script setup lang="ts">

import { Accordion, AccordionItem, AccordionTrigger } from "@/components/ui/accordion"
import { getTeams } from "@/services/team/team.service"
import TeamAccordionContent from "@/components/organisms/teams/TeamAccordionContent.vue"
import { Button } from "@/components/ui/button"
import { Pencil } from "lucide-vue-next"
import EditTeamDialog from "./EditTeamDialog.vue"
import { Column, Row } from "@/components/atoms/containers"
import { useQuery, useQueryClient } from "@tanstack/vue-query"
import { ref } from "vue"
import { StudentSchema, type Student } from "@/types/student"
import { updateStudent, getStudentsByTeamId } from "@/services/student/student.service"
import { cn } from "@/utils/style"
import { Loading } from "@/components/organisms/loading"
import { hasPermission } from "@/services/user/user.service"
import { sendNotificationsByRole } from "@/services/notification/notification.service"
import { getCurrentPhase } from "@/services/project/project.service"
import { Subtitle } from "@/components/atoms/texts"
import SwitchStudentsFlags from "@/components/organisms/teams/switch-student/SwitchStudentsFlags.vue"
import { Cookies } from "@/utils/cookie"

const queryClient = useQueryClient()

const open = ref<boolean[]>([])
const dragging = ref<number | null>(null)
const students = ref<Record<number, Student[]>>()
const isPl = Cookies.getRole() === "PROJECT_LEADER"

const { data: currentPhase } = useQuery({ queryKey: ["current-phase"], queryFn: getCurrentPhase })

const { data: teams, refetch: refetchTeams, isLoading } = useQuery({
	queryKey: ["teams"], queryFn: async() => {
		const teams = await getTeams()

		open.value = teams.map(() => true)

		students.value = {}
		await Promise.all(teams.map(async(team) => {
			const teamStudents = await getStudentsByTeamId(team.id, false)
			students.value = { ...students.value, [team.id]: teamStudents }
		}))

		return teams
	}
})

const handleDrop = async(event: DragEvent, teamId: number) => {
	event.preventDefault()
	dragging.value = null
	if (!students.value || !currentPhase) return

	const data = event.dataTransfer?.getData("text/plain")
	if (!data) return
	const studentData = StudentSchema.safeParse(JSON.parse(data))
	if (!studentData.success) return
	const student = studentData.data

	const originTeam = teams.value?.find(team => students.value?.[team.id]?.find(s => s.id === student.id))
	if (!originTeam || originTeam.id === teamId) return

	students.value = {
		...students.value,
		[originTeam.id]: students.value[originTeam.id].filter(s => s.id !== student.id),
		[teamId]: [...(students.value[teamId] ?? []), student].sort((a, b) => a.id - b.id)
	}
	await updateStudent(student.id.toString(), { teamId })
		.then(() => queryClient.invalidateQueries({ queryKey: ["criteria", teamId] }))
		.then(() => queryClient.invalidateQueries({ queryKey: ["criteria", originTeam.id] }))
		.then(() => queryClient.invalidateQueries({ queryKey: ["average", teamId] }))
		.then(() => queryClient.invalidateQueries({ queryKey: ["average", originTeam.id] }))
		.then(() => {
			if (currentPhase.value === "COMPOSING") return
			void sendNotificationsByRole(`L'étudiant ${student.name} a été déplacé de l'équipe "${originTeam.name}" à l'équipe "${teams.value?.find(t => t.id === teamId)?.name}".`, ["SUPERVISING_STAFF", "TEAM_MEMBER"], "MOVE_STUDENT")
		})
		.then(() => queryClient.invalidateQueries({ queryKey: ["notifications"] }))
}

const handleDragEnter = (event: DragEvent, teamId: number) => {
	event.preventDefault()
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
	dragging.value = teamId
}

const handleDragLeave = (event: DragEvent) => {
	event.preventDefault()
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
	dragging.value = null
}

const style = (teamId: number) => cn(
	"rounded-md border px-4 bg-white",
	{ "border-dashed border-x-light-blue border-t-light-blue border-b-light-blue": dragging.value === teamId }
)

const canEdit = hasPermission("TEAM_MANAGEMENT")
const canSeeStudentFlags = hasPermission("FLAG_TEAM_WITH_STUDENTS")

</script>

<template>
	<Loading v-if="isLoading" />
	<Column v-else>
		<SwitchStudentsFlags v-if="canSeeStudentFlags" :isPl="isPl" />
		<Accordion type="multiple" :default-value="teams && teams.map(team => team.id.toString())" class="space-y-4">
			<Row v-for="(team, i) in teams" :key="team.id" class="w-full items-start gap-8">
				<AccordionItem :value="team.id.toString()" class="flex-1" :class="style(team.id)"
					v-on:drop="(e: DragEvent) => handleDrop(e, team.id)"
					v-on:dragenter="(e: DragEvent) => handleDragEnter(e, team.id)"
					v-on:dragover="(e: DragEvent) => handleDragEnter(e, team.id)" v-on:dragleave="handleDragLeave"
					v-model:open="open[i]"
				>
					<AccordionTrigger>
						<Row class="items-center justify-between w-full mr-4">
							<Subtitle>
								{{ team.name }}
								{{ team.leader?.name ? `(${team.leader.name})` : "" }}
							</Subtitle>
							<EditTeamDialog v-if="canEdit" :team="team" @edit:team="refetchTeams">
								<Button variant="ghost" size="icon" @click="() => open[i] = !open[i]">
									<Pencil class="w-4" />
								</Button>
							</EditTeamDialog>
						</Row>
					</AccordionTrigger>
					<TeamAccordionContent :team-id="team.id" :students="(students && students[team.id]) ?? null" />
				</AccordionItem>
			</Row>
		</Accordion>
	</Column>
</template>