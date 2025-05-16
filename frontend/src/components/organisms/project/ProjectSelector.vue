<script setup lang="ts">
import { ref, onMounted } from "vue"
import { Button } from "@/components/ui/button"
import { createToast } from "@/utils/toast"
import { Cookies } from "@/utils/cookie/cookie.util"
import { Select, SelectTrigger, SelectValue, SelectContent, SelectItem } from "@/components/ui/select"
import { type Project } from "@/types/project"
import { setActualProject } from "@/services/project"
import { Subtitle } from "@/components/atoms/texts"
import { Row } from "@/components/atoms/containers"
import ProjectAdd from "./ProjectAdd.vue"

const emits = defineEmits(["choose:project", "add:project"])

const props = defineProps<{
	projects: Array<Project>
}>()

const selectedProjectId = ref<string | null>(null)

const handleSelectChange = (value: string) => {
	selectedProjectId.value = value
}

const getNameById = (id: string | null): string => {
	if (id === null) return ""
	const project = props.projects.find(project => project.id === Number(id))
	return project?.name ?? ""
}

const handleValidate = () => {
	if (selectedProjectId.value !== null) {
		setActualProject(Number(selectedProjectId.value))
			.then(() => {
				emits("choose:project")
				Cookies.setProjectId(Number(selectedProjectId.value))
				createToast("Projet " + getNameById(selectedProjectId.value) + " sélectionné")
			})
	} else {
		createToast("Veuillez sélectionner un projet")
	}
}

onMounted(() => {
	const currentProjectId = Cookies.getProjectId()
	if (currentProjectId !== undefined && currentProjectId !== null) {
		selectedProjectId.value = currentProjectId.toString()
	}
})
</script>

<template>
	<Row class="items-center justify-between gap-4">
		<Row class="items-center justify-start gap-4">
			<Subtitle>Projet actuel :</Subtitle>

			<Select :model-value="selectedProjectId ?? undefined" @update:model-value="handleSelectChange">
				<SelectTrigger class="w-64">
					<SelectValue />
				</SelectTrigger>
				<SelectContent>
					<SelectItem v-for="project in projects" :key="project.id" :value="project.id.toString()">
						{{ project.name }}
					</SelectItem>
				</SelectContent>
			</Select>

			<Button @click="handleValidate" variant="outline">Valider</Button>
		</Row>
		<ProjectAdd @add:project="emits('add:project')" />
	</Row>
</template>