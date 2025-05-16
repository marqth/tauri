<script setup lang="ts">

import { ref } from "vue"
import { useMutation } from "@tanstack/vue-query"
import { createToast } from "@/utils/toast"
import { Trash2 } from "lucide-vue-next"
import { deleteProject } from "@/services/project/project.service"
import { Button } from "@/components/ui/button"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Table, TableBody, TableCell, TableRow, TableHead, TableHeader } from "@/components/ui/table"
import { type Project } from "@/types/project"
import { Column } from "@/components/atoms/containers"
import { Subtitle } from "@/components/atoms/texts"
import { cn } from "@/utils/style"
import { CheckIcon } from "@/components/atoms/icons"

defineProps<{
	projects: Array<Project>
}>()

const rowClass = cn("py-2 h-auto")

const emits = defineEmits(["delete:project"])
const projectToDelete = ref<number | null>(null)
const isDialogOpen = ref<boolean>(false)

const { mutate: deleteProjectMutate } = useMutation({
	mutationKey: ["delete-project"],
	mutationFn: async() => {
		if (projectToDelete.value !== null) {
			await deleteProject(projectToDelete.value)
				.then(() => {
					createToast("Le projet a été supprimé.")
					isDialogOpen.value = false
					emits("delete:project")
				})
				.catch(() => {
					createToast("Erreur lors de la suppression du projet.")
				})
		}
	}
})
</script>

<template>
	<Column class="gap-2">
		<Subtitle>Liste des projets existants</Subtitle>

		<Table v-if="projects">
			<TableHeader class="h-fit">
				<TableRow class="h-10 pb-1">
					<TableHead :class="rowClass" class="min-w-36">Nom</TableHead>
					<TableHead :class="rowClass" class="min-w-36">Nombre d'équipes</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Phase</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Projet actuel</TableHead>
					<TableHead :class="rowClass" class="w-10"></TableHead>
				</TableRow>
			</TableHeader>
			<TableBody v-if="projects">
				<TableRow v-for="project in projects" :key="project.id">
					<TableCell class="font-medium min-w-36" :class="rowClass">
						{{ project.name }}
					</TableCell>
					<TableCell class="min-w-36" :class="rowClass">
						{{ project.nbTeams ? project.nbTeams : "Pas encore générées" }}
					</TableCell>
					<TableCell class="min-w-28" :class="rowClass">
						{{ project.phase }}
					</TableCell>
					<TableCell class="min-w-28" :class="rowClass">
						<CheckIcon :checked="project.actual" />
					</TableCell>
					<TableCell :class="rowClass">
						<Dialog v-model:open="isDialogOpen" v-if="!project.actual">
							<DialogTrigger as-child>
								<Trash2 class="stroke-gray-600 mr-2 h-4 w-4 hover:stroke-primary transition-colors cursor-pointer" />
							</DialogTrigger>
							<DialogContent class="sm:max-w-[425px]">
								<DialogHeader>
									<DialogTitle>Supprimer le projet</DialogTitle>
									<DialogDescription>
										Êtes-vous sûr de vouloir supprimer ce projet ? Cette action est irréversible.
									</DialogDescription>
								</DialogHeader>
								<DialogFooter class="space-x-2">
									<Button @click="isDialogOpen = false" variant="outline">Annuler</Button>
									<Button @click="deleteProjectMutate" variant="destructive">
										Supprimer
									</Button>
								</DialogFooter>
							</DialogContent>
						</Dialog>
					</TableCell>
				</TableRow>
			</TableBody>
		</Table>
	</Column>
</template>