<script setup lang="ts">

import { Column, Row } from "@/components/atoms/containers"
import { Skeleton } from "@/components/ui/skeleton"
import { Input } from "@/components/ui/input"
import { CustomDialog } from "@/components/molecules/dialog"
import { LoadingButton } from "@/components/molecules/buttons"
import { updateGradeType } from "@/services/grade-type/grade-type.service"
import { ErrorText } from "@/components/atoms/texts"
import type { GradeType } from "@/types/grade-type"
import { computed, ref } from "vue"
import { DialogClose } from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { useMutation } from "@tanstack/vue-query"
import { Label } from "@/components/ui/label"
import { createToast } from "@/utils/toast"

const DIALOG_TITLE = "Modifier les coefficients"
const DIALOG_DESCRIPTION = "Les coefficients des notes importées sont utilisés lors de la génération des équipes."

const open = ref(false)

const props = defineProps<{
	gradeTypes: GradeType[] | null
}>()

let filteredGradeTypes = computed(() => props.gradeTypes?.filter(gradeType => gradeType.name !== "Moyenne"))

const onInputValueChange = (e: Event, gradeTypeId: number) => {
	if (!filteredGradeTypes.value) return
	const input = e.target as HTMLInputElement
	const value = parseFloat(input.value)
	if (isNaN(value) || value < 0) return

	const gradeTypeIndex = filteredGradeTypes.value?.findIndex(gradeType => gradeType.id === gradeTypeId)
	if (gradeTypeIndex === -1) return

	filteredGradeTypes.value[gradeTypeIndex] = { ...filteredGradeTypes.value[gradeTypeIndex], factor: value }
}

const emits = defineEmits(["update:factors"])

//TODO Pour optimiser le code : Envoyer une requête d'update du coeff SEULEMENT s'il a changé (système de old coeff / new coeff ?)
const { isPending, error, mutate: update } = useMutation({ mutationKey: ["update-grade-factors"], mutationFn: async() => {
	if (!filteredGradeTypes.value) return
	await Promise.all(filteredGradeTypes.value.map(async(gradeType) => {
		for (let i = 0; i < 10; i++) {
			let stop = true
			await updateGradeType(gradeType.id, { factor: gradeType.factor })
				.catch(() => stop = false)
				.finally(() => {
					if (stop) return
				})
		}
	}))
		.then(() => open.value = false)
		.then(() => emits("update:factors"))
		.then(() => createToast("Les coefficients ont été mis à jour."))
} })

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Column v-if="filteredGradeTypes" class="items-stretch gap-2">
			<Row v-for="(gradeType, i) in filteredGradeTypes" :key="i" class="items-center">
				<Label :for="gradeType.name" class="w-1/2">{{ gradeType.name }}</Label>
				<Input
					:id="gradeType.name" class="w-1/2" :default-value="gradeType.factor ?? 1"
					:onchange="(e: Event) => onInputValueChange(e, gradeType.id)"
				/>
			</Row>
		</Column>
		<Skeleton v-else class="w-full h-56" />
		<ErrorText v-if="error">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="update" :loading="isPending">
				Mettre à jour
			</LoadingButton>
		</template>
	</CustomDialog>
</template>