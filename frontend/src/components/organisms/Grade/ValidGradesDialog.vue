<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ref, watch } from "vue"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { LoadingButton } from "@/components/molecules/buttons"
import { createToast } from "@/utils/toast"
import { setGradesConfirmation, getIndividualGradesByTeam } from "@/services/grade/grade.service"
import { setValidationBonusesByTeam, getValidationBonusesByTeam } from "@/services/bonus/bonus.service"
import { Cookies } from "@/utils/cookie"
import { sendNotificationsByTeam } from "@/services/notification"

const emits = defineEmits(["valid:individual-grades", "valid:limited-bonus"])
const open = ref(false)

const props = defineProps<{
	selectedTeam?: string
	selectedSprint?: string
}>()

const queryClient = useQueryClient()
const selectedTeam = ref(props.selectedTeam)
const selectedSprint = ref(props.selectedSprint)


const fetchIndividualGradesByTeam = async() => {
	if (selectedTeam.value && selectedSprint.value) {
		const data = await getIndividualGradesByTeam(Number(selectedSprint.value), Number(selectedTeam.value))
		const authorMap = {}
		data.forEach(grade => {
			if (grade.author == null) {
				return
			}
			const authorId = grade.author.id
			if (!authorMap[authorId]) {
				authorMap[authorId] = { name: grade.author.name, count: 0, validCount: 0, students: [] }
			}
			authorMap[authorId].count++
			if (grade.confirmed) {
				authorMap[authorId].validCount++
			}
			authorMap[authorId].students.push({ id: grade.student.id, name: grade.student.name, grade: grade.value })
		})
		return Object.values(authorMap)
	}
}


const fetchValidationBonusesByTeam = async() => {
	if (selectedTeam.value) {
		const data = await getValidationBonusesByTeam(Number(selectedTeam.value), Number(selectedSprint.value))
		const bonusMap = {}

		data.forEach(record => {
			const bonusId = record.bonus.id
			if (!bonusMap[bonusId]) {
				bonusMap[bonusId] = {
					bonus: record.bonus,
					authorCount: 0,
					authors: []
				}
			}
			bonusMap[bonusId].authorCount++
			bonusMap[bonusId].authors.push({
				id: record.author.id,
				name: record.author.name
			})
		})

		if (bonusMap[data[0].bonus.id].authorCount == 9) {
			await sendNotificationsByTeam("Bonus limités validé par tous les membres de l'équipe", parseInt(selectedTeam.value), "BONUS_MALUS", false)
				.then(() => queryClient.invalidateQueries({ queryKey: ["notifications"] }))
		}

		return Object.values(bonusMap)
	}
}


const { data: individualGradesByTeam, refetch: refetchIndividualGradesByTeam } = useQuery({
	queryKey: ["individual-grade-team", selectedTeam, selectedSprint],
	queryFn: fetchIndividualGradesByTeam,
	enabled: !!selectedTeam.value && !!selectedSprint.value
})


const { data: validationBonusesByTeam, refetch: refetchValidationBonusesByTeam } = useQuery({
	queryKey: ["validation-bonuses-team", selectedTeam, selectedSprint],
	queryFn: fetchValidationBonusesByTeam,
	enabled: !!selectedTeam.value && !!selectedSprint.value
})


watch(
	() => [props.selectedTeam, props.selectedSprint],
	([newTeam, newSprint]) => {
		selectedTeam.value = newTeam
		selectedSprint.value = newSprint
		refetchIndividualGradesByTeam()
		refetchValidationBonusesByTeam()
	}
)


const { mutate: mutateIndividual, isPending: isPendingIndividual, error: errorIndividual } = useMutation({
	mutationKey: ["individual-grades"], mutationFn: async() => {
		await setGradesConfirmation(Number(props.selectedTeam), Number(props.selectedSprint))
			.then(() => {
				emits("valid:individual-grades")
				createToast("Les notes individuelles ont été validées.")
				refetchIndividualGradesByTeam()
				refetchValidationBonusesByTeam()
			})
	}
})


const { mutate: mutateBonuses, isPending: isPendingBonuses, error: errorBonuses } = useMutation({
	mutationKey: ["limited-bonus"], mutationFn: async() => {
		await setValidationBonusesByTeam(Number(props.selectedTeam), Number(props.selectedSprint), Cookies.getUserId())
			.then(() => {
				emits("valid:limited-bonus")
				createToast("Les bonus limités ont été validées.")
				refetchIndividualGradesByTeam()
				refetchValidationBonusesByTeam()
			})
	}
})


const DIALOG_TITLE = "Valider les notes individuelles"
const DIALOG_DESCRIPTION
	= "Vous pouvez ici valider les bonus limité, et les notes individuelles."

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<ErrorText v-if="errorBonuses || errorIndividual" class="mb-2">Une erreur est survenue.</ErrorText>


		<div v-if="individualGradesByTeam?.length > 0">
			<p v-for="(author, index) in individualGradesByTeam" :key="index">{{ author.count }}
				note(s) attribuée(s) par {{ author.name }}, {{ author.validCount }} validées</p>
		</div>
		<div v-else>
			<p>Aucune note individuelle n'a été attribuées.</p>
		</div>

		<div v-if="validationBonusesByTeam?.length > 0">
			<p v-for="(bonusRecord, index) in validationBonusesByTeam" :key="index">Bonus de {{ bonusRecord.bonus.value
				}} pour {{ bonusRecord.bonus.student.name }}, {{ bonusRecord.authorCount }} validation(s)</p>
		</div>
		<div v-else>
			<p>Aucun bonus limité n'a été attribué.</p>
		</div>

		<template #footer>
			<DialogClose v-if="!isPendingBonuses || !isPendingIndividual">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutateBonuses" :loading="isPendingBonuses">
				Valider les bonus limités
			</LoadingButton>
			<LoadingButton v-if="Cookies.getRole() == 'SUPERVISING_STAFF'" type="submit" @click="mutateIndividual"
				:loading="isPendingIndividual">
				Valider les notes individuelles
			</LoadingButton>
		</template>
	</CustomDialog>
</template>