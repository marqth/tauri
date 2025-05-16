<script setup lang="ts">

import { ref } from "vue"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { ErrorText } from "@/components/atoms/texts"
import { Button } from "@/components/ui/button"
import { Column, Row } from "@/components/atoms/containers"
import { useMutation, useQuery } from "@tanstack/vue-query"
import LoadingButton from "@/components/molecules/buttons/LoadingButton.vue"
import { Select, SelectContent, SelectGroup, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { createToast } from "@/utils/toast"
import { Label } from "@/components/ui/label"
import { getAllUnimportedGradeTypes, uploadGradeScaleTXT } from "@/services/grade-type"
import { UploadArea } from "@/components/molecules/upload-area"

const open = ref(false)
const emits = defineEmits(["add:grade-scale"])

const currentGradeTypeId = ref<string>("")
const file = ref<File | null>(null)

const { data: gradeTypes, refetch } = useQuery({
	queryKey: ["grade-types-without-scale"],
	queryFn: async() => {
		const gradeTypes = await getAllUnimportedGradeTypes()
		return gradeTypes.filter(g => g.scaleTXTBlob === null || g.scaleTXTBlob === undefined)
	}
})

const { error, isPending, mutate: add } = useMutation({ mutationFn: async() => {
	if (currentGradeTypeId.value === "" || !file.value) return

	await uploadGradeScaleTXT(currentGradeTypeId.value, file.value)
		.then(() => open.value = false)
		.then(() => file.value = null)
		.then(() => currentGradeTypeId.value = "")
		.then(() => emits("add:grade-scale"))
		.then(() => createToast("Le barème a bien été ajouté."))
		.then(() => void refetch())
}
})

const DIALOG_TITLE = "Ajouter un barème"
const DIALOG_DESCRIPTION = "Pour ajouter un barème, vous devez spécifier le type de note que vous voulez associer à ce barème, puis importer le fichier correspondant."

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Column>
			<Row class="items-center justify-between my-2">
				<Label>Type de note</Label>

				<Select v-model="currentGradeTypeId">
					<SelectTrigger class="w-[250px]">
						<SelectValue placeholder="Sélectionnez le type de note" />
					</SelectTrigger>
					<SelectContent>
						<SelectGroup>
							<SelectItem v-for="gradeType in gradeTypes" :key="gradeType.id" :value="gradeType.id.toString()">
								{{ gradeType.name }}
							</SelectItem>
						</SelectGroup>
					</SelectContent>
				</Select>
			</Row>
		</Column>

		<UploadArea v-model="file" extension="txt" />

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue lors de l'ajout du barème.</ErrorText>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" class="flex items-center" :loading="isPending" @click="add" :disabled="currentGradeTypeId === '' || file === null">
				Continuer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>