<script setup lang="ts">
import { ref } from 'vue'
import { Button } from "@/components/ui/button"
import { createProject } from "@/services/project/project.service"
import { useMutation } from "@tanstack/vue-query"
import { createToast } from "@/utils/toast"
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog'

import { type CreateProject, ProjectPhaseSchema } from '@/types/project'

const emits = defineEmits(["add:project"])
const isDialogOpen = ref<boolean>(false)
const projectName = ref<string>('')

const { error, mutate } = useMutation({
  mutationKey: ["add-project"],
  mutationFn: async () => {
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
        emits('add:project')
        isDialogOpen.value = false
      })
      .catch(() => createToast("Erreur lors de la création d'un nouveau projet"))
  }
})
</script>

<template>
  <div class="flex justify-center mt-10">
    <Dialog v-model:open="isDialogOpen">
      <DialogTrigger as-child>
        <Button class="">Ajouter projet</Button>
      </DialogTrigger>
      <DialogContent class="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>Confirmer l'ajout</DialogTitle>
          <DialogDescription>
            Êtes-vous sûr de vouloir ajouter un nouveau projet ?
          </DialogDescription>
        </DialogHeader>
        <div class="mb-4">
          <label for="project-name" class="block text-sm font-medium text-gray-700">Nom du projet</label>
          <input id="project-name" v-model="projectName" class="mt-1 block w-full rounded-md" />
        </div>
        <DialogFooter>
          <Button class="bg-red-500 hover:bg-red-600" @click="isDialogOpen = false">Annuler</Button>
          <Button @click="mutate">Confirmer</Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
    <p v-if="error">Erreur lors de la création d'un projet</p>
  </div>
</template>
