<script setup lang="ts">
import { ref } from "vue"
import { Button } from "@/components/ui/button"
import { createProject } from "@/services/project/project.service"
import { useMutation } from "@tanstack/vue-query"
import { createToast } from "@/utils/toast"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"

import { type CreateProject, ProjectPhaseSchema } from "@/types/project"
import Column from "@/components/atoms/containers/Column.vue"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Row } from "@/components/atoms/containers"

const emits = defineEmits(["add:project"])
const isDialogOpen = ref<boolean>(false)
const projectName = ref<string>("")

const { error, mutate } = useMutation({
	mutationKey: ["add-project"],
	mutationFn: async() => {
		const project: CreateProject = {
			nbTeams: 0,
			nbWomen: 0,
			name: projectName.value || "Nouveau projet",
			actual: false,
			phase: ProjectPhaseSchema.parse("COMPOSING")
		}

		await createProject(project)
			.then(() => {
				createToast("Nouveau projet ajouté")
				emits("add:project")
				isDialogOpen.value = false
			})
			.catch(() => createToast("Erreur lors de la création d'un nouveau projet"))
	}
})
</script>

<template>
	<Dialog v-model:open="isDialogOpen">
		<DialogTrigger as-child>
			<Button class="">Ajouter un projet</Button>
		</DialogTrigger>
		<DialogContent class="sm:max-w-[425px]">
			<DialogHeader>
				<DialogTitle>Ajouter un projet</DialogTitle>
				<DialogDescription>
					Pour ajouter un nouveau projet, il vous suffit de renseigner son nom.
				</DialogDescription>
			</DialogHeader>
			<Row class="items-center my-2">
				<Label for="project-name" class="w-1/3">Nom du projet</Label>
				<Input id="project-name" v-model="projectName" class="w-2/3" />
			</Row>
			<p v-if="error">Erreur lors de la création d'un projet</p>
			<DialogFooter class="space-x-2">
				<Button @click="isDialogOpen = false" variant="outline">Annuler</Button>
				<Button @click="mutate">Confirmer</Button>
			</DialogFooter>
		</DialogContent>
	</Dialog>
</template>