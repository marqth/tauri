<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ref } from "vue"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText, Text } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { Textarea } from "@/components/ui/textarea"
import { createReportingFlag } from "@/services/flag"
import { createToast } from "@/utils/toast"
import { Column, Row } from "@/components/atoms/containers"
import {
	Select,
	SelectTrigger,
	SelectValue,
	SelectContent,
	SelectGroup,
	SelectItem,
	SelectLabel
} from "@/components/ui/select"
import { useQuery, useQueryClient } from "@tanstack/vue-query"
import { getTeamByUserId } from "@/services/team/team.service"
import { Cookies } from "@/utils/cookie"
import { getAllStudents } from "@/services/student"
import { getTeams } from "@/services/team"
import type { Team } from "@/types/team"
import type { Student } from "@/types/student"
import { ArrowLeftRight } from "lucide-vue-next"

const open = ref(false)
const description = ref("")
const othersTeams = ref<Team[]>([])
const myTeam  = ref<Team | null>(null)
const selectedMyTeamStudent = ref("")
const selectedOtherTeamStudent = ref("")
const userId = Cookies.getUserId()
const client = useQueryClient()

const { mutate, isPending, error } = useMutation({ mutationKey: ["signal-teams"], mutationFn: async() => {
	if (!description.value) return
	await createReportingFlag(description.value, Number(selectedMyTeamStudent.value), Number(selectedOtherTeamStudent.value))
		.then(() => open.value = false)
		.then(() => description.value = "")
		.then(() => selectedMyTeamStudent.value = "")
		.then(() => selectedOtherTeamStudent.value = "")
		.then(() => client.invalidateQueries({ queryKey: ["flagForConcerned"] }))
		.then(() => createToast("Le signalement a été envoyé."))
} })

const { data: students } = useQuery({ queryKey: ["students"], queryFn: async() => {
	myTeam.value = await getTeamByUserId(userId)
	const students = await getAllStudents()
	let myTeamStudents: Student[] = []
	if (myTeam.value) {
		othersTeams.value = (await getTeams()).filter(team => team.id !== myTeam.value!.id)
		myTeamStudents = filteredStudents(students, myTeam.value!.id)
	}
	return { myTeamStudents, students }
} })

const filteredStudents = (students: Student[], teamId: number) => {
	return students.filter(student => student.team!.id === teamId)
}

const DIALOG_TITLE = "Signaler la composition des équipes"
const DIALOG_DESCRIPTION = "Envoyez un signalement sur la composition des équipes au project leader."

</script>

<template>
  <CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
    <template #trigger>
      <slot />
    </template>

    <Text class="-mb-2">Votre commentaire</Text>
    <Textarea v-model="description" placeholder="Ajouter un commentaire" class="max-h-64"></Textarea>
    <ErrorText v-if="error" class="mt-2">Une erreur est survenue.</ErrorText>
    <Column>
      <Text class="mb-3">Choisie les étudiant(e)s concernés par ce changement</Text>
    <Row class="justify-between items-center">
      <Column>
        <Select v-model="selectedMyTeamStudent" label="Étudiant">
          <SelectTrigger class="w-[180px]">
            <SelectValue placeholder="Votre équipe" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup >
              <SelectItem v-for="student in students?.myTeamStudents" :key="student.id" :label="student.name" :value="student.id.toString()">
                {{ student.name }}
              </SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
      </Column>
      <ArrowLeftRight/>
      <Column>
        <Select v-model="selectedOtherTeamStudent" label="Étudiant">
          <SelectTrigger class="w-[180px]">
            <SelectValue placeholder="Autre équipes" />
          </SelectTrigger>
          <SelectContent>
            <SelectGroup v-for="team in othersTeams" :key="team.id" :label="team.name" :value="team.id">
              <SelectLabel>{{ team.name }}</SelectLabel>
              <SelectItem v-for="student in filteredStudents(students!.students, team.id)" :key="student.id" :label="student.name" :value="student.id.toString()">
                {{ student.name }}
              </SelectItem>
            </SelectGroup>
          </SelectContent>
        </Select>
      </Column>
    </Row>
    </Column>

    <template #footer>
      <DialogClose v-if="!isPending">
        <Button variant="outline">Annuler</Button>
      </DialogClose>
      <LoadingButton type="submit" @click="mutate" :loading="isPending" :disabled="description === '' || selectedMyTeamStudent === '' || selectedOtherTeamStudent === ''">
        Confirmer
      </LoadingButton>
    </template>
  </CustomDialog>
</template>