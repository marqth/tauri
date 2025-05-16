<script setup lang="ts">

import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { Column, Row } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { getStudentsByTeamId } from "@/services/student"
import { Label } from "@/components/ui/label"
import { getStudentBonus, updateBonus } from "@/services/bonus"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { Skeleton } from "@/components/ui/skeleton"
import { ErrorText } from "@/components/atoms/texts"
import { reactive, ref, watch } from "vue"
import { LoadingButton } from "@/components/molecules/buttons"
import { createToast } from "@/utils/toast"
import type { Bonus } from "@/types/bonus"

const props = defineProps<{
	limited: boolean
	teamId: string
	sprintId: string
}>()

const open = ref(false)
let updatedStudentBonuses: Bonus[] = reactive([])

const { data: teamStudents } = useQuery({ queryKey: ["team-students", props.teamId], queryFn: async() => getStudentsByTeamId(Number(props.teamId), true) })

const { data: studentBonuses, refetch: refetchBonuses } = useQuery({
	queryKey: ["student-bonuses", props.teamId, props.sprintId],
	queryFn: async() => {
		if (!teamStudents.value) return
		return await Promise.all(teamStudents.value.map(student => getStudentBonus(student.id, props.limited, props.sprintId)))
	}
})

watch(teamStudents, async(value, _) => {
	if (value) {
		await refetchBonuses()
	}
})

const handleBonusInput = (event: InputEvent, index: number, inputType: "value" | "comment") => {
	if (!studentBonuses.value) return

	let updatedBonusIndex = updatedStudentBonuses.findIndex(bonus => bonus.id === studentBonuses.value[index].id)

	if (updatedBonusIndex === -1) {
		// Clone the bonus and add it to updatedStudentBonuses
		updatedStudentBonuses.push({ ...studentBonuses.value[index] })
		updatedBonusIndex = updatedStudentBonuses.length - 1
	}

	const inputValue = (event.target as HTMLInputElement).value

	if (inputType === "value") {
		const value = parseFloat(inputValue)

		if (isNaN(value)) updatedStudentBonuses[updatedBonusIndex].value = 0

		if (!props.limited) {
			updatedStudentBonuses[updatedBonusIndex].value = value
		} else if (value > 4) {
			updatedStudentBonuses[updatedBonusIndex].value = 4
		} else if (value < -4) {
			updatedStudentBonuses[updatedBonusIndex].value = -4
		} else {
			updatedStudentBonuses[updatedBonusIndex].value = value
		}
	} else if (inputType === "comment") {
		updatedStudentBonuses[updatedBonusIndex].comment = inputValue
	}

	// Check if the updated bonus is identical to the original bonus
	if (JSON.stringify(updatedStudentBonuses[updatedBonusIndex]) === JSON.stringify(studentBonuses.value[index])) {
		// Remove the bonus from updatedStudentBonuses
		updatedStudentBonuses.splice(updatedBonusIndex, 1)
	}
}

const { isPending, error, mutate: update } = useMutation({ mutationKey: ["update-bonuses"], mutationFn: async() => {
	await Promise.all(updatedStudentBonuses.map(studentBonus => updateBonus(studentBonus.id, { studentId: studentBonus.id, value: studentBonus.value, comment: studentBonus.comment })))
		.then(() => open.value = false)
		.then(() => createToast("Les bonus ont été mis à jour."))
		.then(() => refetchBonuses())
} })

const DIALOG_DESCRIPTION_LIMITE = "Vous pouvez ajuster les bonus et malus des membres de votre équipe. Attention, une fois que vous les avez validés, "
	+ "vous ne pouvez plus les modifier. Cependant, si un membre de votre équipe modifie de nouveau les bonus, vous pourrez les valider à nouveau."

const DIALOG_DESCRIPTION_ILLIMITE = "Vous pouvez ajouter des bonus et malus illimités à tous les membres de votre équipe. Cependant, la note finale ne pourra pas être en dessous de 0 ou au dessus de 20."

</script>

<template>
	<CustomDialog title="Bonus et malus de votre équipe" v-model:open="open"
		:description="props.limited ? DIALOG_DESCRIPTION_LIMITE : DIALOG_DESCRIPTION_ILLIMITE">
		<template #trigger>
			<Button variant="default">Voir les bonus</Button>
		</template>

		<Row v-if="studentBonuses" class="flex-wrap">
			<Row v-for="(student, index) in teamStudents" :key="student.id" class="mb-3 w-1/2">
				<Column>
					<Row class="grid grid-cols-[3fr,1fr] mr-2">
						<Label :for="student.name" class="whitespace-nowrap mt-3">{{ student.name }}</Label>
						<Input class="mb-2 " type="number"
							:default-value="studentBonuses[index]?.value === 0 ? '' : studentBonuses[index]?.value"
							:onchange="(e: InputEvent) => handleBonusInput(e, index, 'value')" />
					</Row>
					<Row class="mr-2">
						<Input v-if="!props.limited" type="text" :default-value="studentBonuses[index]?.comment ?? ''"
							:onchange="(e: InputEvent) => handleBonusInput(e, index, 'comment')" />
					</Row>
				</Column>
			</Row>
		</Row>
		<Skeleton v-else class="w-full h-56" />
		<ErrorText v-if="error">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="update" :loading="isPending">
				Valider
			</LoadingButton>
		</template>
	</CustomDialog>
</template>
<style scoped></style>