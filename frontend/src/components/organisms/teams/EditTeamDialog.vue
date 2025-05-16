<script setup lang="ts">

import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"
import { Column, Row } from "@/components/atoms/containers"
import Input from "@/components/ui/input/Input.vue"
import Label from "@/components/ui/label/Label.vue"
import { updateTeam } from "@/services/team/team.service"
import { ref } from "vue"
import type { Team } from "@/types/team"
import { Skeleton } from "@/components/ui/skeleton"
import { getUsersByRole } from "@/services/user/user.service"
import { CustomSelect } from "@/components/molecules/select"
import { useMutation, useQuery } from "@tanstack/vue-query"
import { LoadingButton } from "@/components/molecules/buttons"
import { ErrorText } from "@/components/atoms/texts"
import { createToast } from "@/utils/toast"

const open = ref(false)
const emits = defineEmits(["edit:team"])

const props = defineProps<{
	team: Team
}>()

const teamName = ref<string>(props.team.name ?? "")
const teamLeaderId = ref<string | undefined>(props.team.leader?.id.toString())

const { data: supervisors } = useQuery({ queryKey: ["supervisors"], queryFn: () => getUsersByRole("SUPERVISING_STAFF") })

const { mutate, error, isPending } = useMutation({ mutationKey: ["edit-team"], mutationFn: async() => {
	await updateTeam(props.team.id, { name: teamName.value, leaderId: parseInt(teamLeaderId.value ?? "") ?? undefined })
		.then(() => open.value = false)
		.then(() => emits("edit:team"))
		.then(() => createToast(`L'équipe ${teamName.value} a été modifiée.`))
} })

const DIALOG_TITLE = "Modifier une équipe"
const DIALOG_DESCRIPTION = "Vous pouvez modifier le nom de l'équipe ainsi que son professeur référent."

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Column class="items-stretch gap-2" v-if="team && supervisors">
			<Row class="items-center">
				<Label for="teamName" class="w-2/5 text-left">Nom de l'équipe</Label>
				<Input id="teamName" type="text" v-model="teamName" class="w-3/5" />
			</Row>
			<Row class="items-center">
				<Label for="teamLeader" class="w-2/5 text-left">Professeur référent</Label>
				<CustomSelect
					class="w-3/5" placeholder="Sélectionnez un professeur" v-model="teamLeaderId"
					:data="supervisors" :getId="supervisor => supervisor.id.toString()" :getName="supervisor => supervisor.name"
				/>
			</Row>
		</Column>
		<Skeleton v-else class="w-full h-16" />
		<ErrorText v-if="error" class="mb-2">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose v-if="!isPending">
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" @click="mutate" :loading="isPending">
				Confirmer
			</LoadingButton>
		</template>
	</CustomDialog>
</template>