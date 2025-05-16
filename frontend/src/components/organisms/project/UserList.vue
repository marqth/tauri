<script setup lang="ts">

import { ref, watch } from "vue"
import { Column } from "@/components/atoms/containers"
import { Button } from "@/components/ui/button"
import { useMutation } from "@tanstack/vue-query"
import { createToast } from "@/utils/toast"
import { type User } from "@/types/user"
import { deleteUser } from "@/services/user/user.service"
import { formatRole, type RoleType } from "@/types/role"
import { Table, TableBody, TableCell, TableRow, TableHead, TableHeader } from "@/components/ui/table"
import {
	Dialog,
	DialogContent,
	DialogDescription,
	DialogFooter,
	DialogHeader,
	DialogTitle,
	DialogTrigger
} from "@/components/ui/dialog"
import { Subtitle } from "@/components/atoms/texts"
import { cn } from "@/utils/style"
import { Trash2 } from "lucide-vue-next"

defineProps<{
	users: Array<User & { role: RoleType[] }>
	usersLoading: boolean
	usersError: boolean
	rolesLoading: boolean
	rolesError: boolean
}>()


const emits = defineEmits(["delete:user"])
const userToDelete = ref<number | null>(null)
const isDialogOpen = ref<boolean>(false)

const rowClass = cn("py-2 h-auto")

const getRoleDescription = (role: RoleType[]) => {
	return role.map(formatRole)
}

const { mutate: deleteUserMutate } = useMutation({
	mutationKey: ["delete-user"],
	mutationFn: async() => {
		if (userToDelete.value !== null) {
			await deleteUser(userToDelete.value)
				.then(() => {
					createToast("L'utilisateur a été supprimé.")
					isDialogOpen.value = false
					emits("delete:user")
				})
				.catch(() => {
					createToast("Erreur lors de la suppression de l'utilisateur.")
				})
		}
	}
})

watch(isDialogOpen, (newVal) => {
	if (!newVal) userToDelete.value = null
})
</script>

<template>
	<Column class="gap-2">
		<Subtitle>Gestion des utilisateurs</Subtitle>
		<div v-if="usersLoading || rolesLoading">Chargement...</div>
		<div v-else-if="usersError || rolesError">Erreur lors du chargement des données.</div>

		<Table v-else>
			<TableHeader class="h-fit">
				<TableRow class="h-10 pb-1">
					<TableHead :class="rowClass" class="min-w-36">Nom</TableHead>
					<TableHead :class="rowClass" class="min-w-36">Email</TableHead>
					<TableHead :class="rowClass" class="min-w-28">Rôle(s)</TableHead>
					<TableHead :class="rowClass" class="w-10"></TableHead>
				</TableRow>
			</TableHeader>
			<TableBody v-if="users">
				<TableRow v-for="user in users.filter(u => u.role[0] !== 'OPTION_STUDENT')" :key="user.id">
					<TableCell class="font-medium min-w-36" :class="rowClass">
						{{ user.name }}
					</TableCell>
					<TableCell class="min-w-36" :class="rowClass">
						{{ user.email }}
					</TableCell>
					<TableCell class="min-w-28" :class="rowClass">
						{{ getRoleDescription(user.role).join(', ') }}
					</TableCell>
					<TableCell :class="rowClass">
						<Dialog v-model:open="isDialogOpen">
							<DialogTrigger as-child>
								<Trash2 class="stroke-gray-600 mr-2 h-4 w-4 hover:stroke-primary transition-colors cursor-pointer"
								@click="userToDelete = user.id" />
							</DialogTrigger>
							<DialogContent>
								<DialogHeader>
									<DialogTitle>Supprimer un utilisateur</DialogTitle>
									<DialogDescription>
										Êtes-vous sûr de vouloir supprimer l'utilisateur {{ user.name }} ?
									</DialogDescription>
								</DialogHeader>
								<DialogFooter class="space-x-2">
									<Button @click="isDialogOpen = false" variant="outline">Annuler</Button>
									<Button @click="deleteUserMutate" variant="destructive">
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