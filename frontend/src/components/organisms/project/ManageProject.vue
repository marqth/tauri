<script setup lang="ts">
import { onMounted, ref } from "vue"
import { Column } from "@/components/atoms/containers"
import ProjectList from "./ProjectList.vue"
import ProjectSelector from "./ProjectSelector.vue"
import { getAllProjects } from "@/services/project/project.service"
import { type Project } from "@/types/project"

const projects = ref<Project[]>([])
const projectsLoading = ref(true)
const projectsError = ref(false)

onMounted(async() => {
	await refetch()
})

const refetch = async() => {
	projectsLoading.value = true
	projectsError.value = false
	try {
		projects.value = await getAllProjects()
	} catch (error) {
		projectsError.value = true
	} finally {
		projectsLoading.value = false
	}
}

</script>

<template>
	<Column class="items-center border rounded-md bg-white px-5 py-4">
		<p v-if="projectsLoading">Chargement...</p>
		<p v-else-if="projectsError">Erreur lors du chargement des projets.</p>
		<Column v-else class="w-full gap-8">
			<ProjectSelector :projects="projects" @choose:project="refetch" @add:project="refetch" />
			<ProjectList :projects="projects" @delete:project="refetch" />
		</Column>
	</Column>
</template>