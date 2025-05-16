<script setup lang="ts">

import { Button } from "@/components/ui/button"
import { deleteAllStudents } from "@/services/student/student.service"
import LoadingButton from "@/components/molecules/buttons/LoadingButton.vue"
import { ref } from "vue"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { useMutation, useQueryClient } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { createToast } from "@/utils/toast"
import { sendNotificationsByRole } from "@/services/notification"
import { Cookies } from "@/utils/cookie"
import getRole = Cookies.getRole;
import type { RoleType } from "@/types/role"

const open = ref(false)
const emits = defineEmits(["delete:students"])
const queryClient = useQueryClient()
const oppositeRole: RoleType[] = getRole() === "OPTION_LEADER" ? ["PROJECT_LEADER"] : ["OPTION_LEADER"]

const { mutate, isPending, error } = useMutation({ mutationKey: ["delete-students"], mutationFn: async() => {
	await deleteAllStudents()
		.then(() => open.value = false)
		.then(() => emits("delete:students"))
		.then(() => createToast("Les étudiants ont été supprimés."))
		.then(() => sendNotificationsByRole("La liste des étudiants a été supprimée.", oppositeRole, "DELETE_STUDENTS"))
		.then(() => queryClient.invalidateQueries({ queryKey: ["notifications"] }))
} })

const DIALOG_TITLE = "Supprimer les étudiants"
const DIALOG_DESCRIPTION = "Êtes-vous bien sûr de vouloir supprimer tous les étudiants de la base de données ?"

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutate" :loading="isPending" variant="destructive">
				Supprimer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>