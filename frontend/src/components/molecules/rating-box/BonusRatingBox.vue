<script setup lang="ts">

import { ErrorText, InfoText, Subtitle } from "@/components/atoms/texts"
import { CheckIcon, Loader } from "@/components/atoms/icons"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Column, Row } from "@/components/atoms/containers"
import { useMutation, useQuery, useQueryClient } from "@tanstack/vue-query"
import { onMounted, ref, watch } from "vue"
import { getStudentsByTeamId } from "@/services/student"
import { getStudentBonus, updateBonus } from "@/services/bonus"
import { createToast } from "@/utils/toast"
import type { Student } from "@/types/student"
import { sendNotificationsByTeam } from "@/services/notification"

const props = defineProps<{
	sprintId: string,
	teamId: string,
	limited: boolean,
}>()

const queryClient = useQueryClient()
const status = ref<"IDLE" | "LOADING" | "DONE">("IDLE")
const teamStudents = ref<Student[] | null>(null)
const bonuses = ref(teamStudents.value ? Array(teamStudents.value.length).fill("") : [])
const comments = ref(teamStudents.value ? Array(teamStudents.value.length).fill("") : [])
const oldValues = ref({ bonuses: JSON.parse(JSON.stringify(bonuses.value)), comments: JSON.parse(JSON.stringify(comments.value)) })
const DIALOG_DESCRIPTION_LIMITE = "Vous pouvez ajuster les bonus et malus des membres de votre équipe. Attention, une fois que vous les avez validés, "
	+ "vous ne pouvez plus les modifier. Cependant, si un membre de votre équipe modifie de nouveau les bonus, vous pourrez les valider à nouveau."

const DIALOG_DESCRIPTION_ILLIMITE = "Vous pouvez ajouter des bonus et malus illimités à tous les membres de votre équipe. Cependant, la note finale ne pourra pas être en dessous de 0 ou au dessus de 20."

const { data: studentBonuses, refetch: refetchBonuses } = useQuery({
	queryKey: ["student-bonuses", props.teamId, props.sprintId, props.limited],
	queryFn: async() => {
		if (!teamStudents.value) return
		return await Promise.all(teamStudents.value.map(student => getStudentBonus(student.id, props.limited, props.sprintId)))
	}
})

const updateBonuses = async() => {
	await refetchBonuses()
	for (let i = 0; i < teamStudents.value.length; i++) {
		const bonus = studentBonuses.value[i]
		if (bonus.value === null) {
			bonuses.value[i] = "0"
		} else {
			bonuses.value[i] = bonus?.value.toString() ?? ""
		}
		comments.value[i] = bonus?.comment ?? ""
	}

	oldValues.value = { bonuses: JSON.parse(JSON.stringify(bonuses.value)), comments: JSON.parse(JSON.stringify(comments.value)) }
	status.value = "IDLE"
}

const onBonusChange = (value: string | number, index: number) => {
	if (!props.limited) return
	const parsedValue = Number(value)
	if (value === "" || isNaN(parsedValue)) {
		bonuses.value[index] = "0"
		return
	}
	if (parsedValue > 4) {
		bonuses.value[index] = "4"
		return
	}
	if (parsedValue < -4) {
		bonuses.value[index] = "-4"
		return
	}
	bonuses.value[index] = parsedValue.toString()
}

const { mutate, isPending, isError } = useMutation({
	mutationFn: async() => {
		const bonusesSum = bonuses.value.reduce((a, b) => a + (Number(b) || 0), 0)
		if (bonusesSum !== 0 && props.limited) {
			createToast("La somme des bonus doit être égale à 0.")
			return
		} else {
			for (let index = 0; index < teamStudents.value.length; index++) {
				if (!(bonuses.value[index] === oldValues.value.bonuses[index] && comments.value[index] === oldValues.value.comments[index])) {
					status.value = "LOADING"
					await updateBonus(studentBonuses.value[index].id, {
						value: Number(bonuses.value[index]),
						comment: comments.value[index]
					})
				}
			}
			createToast("Les bonus ont bien été mis à jour.")
			oldValues.value = { bonuses: JSON.parse(JSON.stringify(bonuses.value)), comments: JSON.parse(JSON.stringify(comments.value)) }
			queryClient.invalidateQueries({ queryKey: ["student-bonuses", props.teamId, props.sprintId, props.limited] })
			if (props.limited) {
				await sendNotificationsByTeam(`Les bonus pour le sprint ${props.sprintId} ont été modifiés`, Number(props.teamId), "BONUS_MALUS", true)
					.then(() => queryClient.invalidateQueries({ queryKey: ["notifications"] }))
			}
		}
	}
})

watch(isPending, (newValue) => {
	if (!newValue && status.value === "LOADING") {
		status.value = "DONE"
	}
})

watch(status, (newValue) => {
	if (newValue === "DONE") {
		setTimeout(() => {
			status.value = "IDLE"
		}, 5000)
	}
})

watch(() => [teamStudents.value, props.sprintId], () => {
	updateBonuses()
})

onMounted(async() => {
	teamStudents.value = await getStudentsByTeamId(Number(props.teamId), true)
	updateBonuses()
})
</script>

<template>
	<Column class="items-stretch justify-start gap-3 border rounded-md p-6 bg-white">
		<Row class="items-center justify-between gap-2">
			<Row class="items-center justify-start gap-2">
				<slot/>
				<Subtitle>Bonus / Malus</Subtitle>
			</Row>
			<Loader v-if="status === 'LOADING'" class="size-4 stroke-slate-500"/>
			<CheckIcon v-if="status === 'DONE'" :checked="!isError"/>
		</Row>

		<Row class="items-center justify-between gap-6">
			<InfoText class="flex-1">{{ props.limited ? DIALOG_DESCRIPTION_LIMITE : DIALOG_DESCRIPTION_ILLIMITE	 }}</InfoText>
		</Row>
		<Row class="flex-wrap gap-4">
			<Column class="w-full gap-2" v-for="(student, index) in teamStudents" :key="student.id">
				<Row class="items-center justify-between gap-6">
					<Subtitle class="w-1/2">{{ student.name }}</Subtitle>
					<Input class="w-16" type="number" v-model="bonuses[index]" @update:model-value="value => onBonusChange(value, index)" :disabled="isPending" v-on:blur="mutate" />
				</Row>
				<Row class="items-stretch justify-between gap-2">
					<Textarea v-if="!props.limited" class="min-h-10 h-10" v-model="comments[index]"  placeholder="Ajouter un commentaire" :disabled="isPending" v-on:blur="mutate" />
				</Row>
			</Column>
		</Row>

		<ErrorText v-if="status === 'DONE' && isError">Une erreur est survenue.</ErrorText>
	</Column>
</template>

<style scoped>

</style>