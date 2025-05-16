<script setup lang="ts">

import { ref } from "vue"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { generateTeams } from "@/services/team"
import { CustomDialog, DialogClose } from "@/components/molecules/dialog"
import { LoadingButton } from "@/components/molecules/buttons"
import { useMutation } from "@tanstack/vue-query"
import { ErrorText } from "@/components/atoms/texts"
import { Column, Row } from "@/components/atoms/containers"
import { createToast } from "@/utils/toast"
import { Switch } from "@/components/ui/switch"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { Info } from "lucide-vue-next"


const nbTeams = ref("6")
const womenPerTeam = ref("1")
const autoWomenRatio = ref(false)
const open = ref(false)

const emits = defineEmits(["generate:teams"])

defineProps<{
	nbStudents: number
}>()

const { mutate, isPending, error } = useMutation({ mutationFn: async() => {
	await generateTeams(nbTeams.value, womenPerTeam.value, autoWomenRatio.value)
		.then(() => open.value = false)
		.then(() => emits("generate:teams"))
		.then(() => createToast("Les équipes ont été générées."))
} })

const DIALOG_TITLE = "Générer les équipes"
const DIALOG_DESCRIPTION = "Modifiez les paramètres de génération, puis cliquez sur le bouton pour générer automatiquement les équipes."

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" :description="DIALOG_DESCRIPTION" v-model:open="open">
		<template #trigger>
			<slot />
		</template>

		<Column class="items-stretch gap-2">
			<Row class="items-center">
				<Label for="nbTeams" class="w-3/5 text-left">Nombre d'équipes</Label>
				<Input id="nbTeams" type="number" v-model="nbTeams" class="w-2/5" :min="0" :max="nbStudents" />
			</Row>
			<Row class="items-center">
				<Label for="womenPerTeam" class="w-3/5 text-left">
					Nombre de femmes par équipe
					<TooltipProvider :delay-duration="200">
						<Tooltip>
							<TooltipTrigger>
								<Info class="size-4" />
							</TooltipTrigger>
							<TooltipContent>
								<p class="max-w-96">
									Si ce n'est pas possible d'avoir autant de femmes par équipe, alors les femmes seront d'abord réparties dans les premières équipes, de manière à ce qu'aucune femme ne soit dans les dernières équipes.
								</p>
							</TooltipContent>
						</Tooltip>
					</TooltipProvider>
				</Label>
				<Input id="womenPerTeam" type="number" v-model="womenPerTeam" class="w-2/5" :min="0" :max="nbStudents" :disabled="autoWomenRatio" />
			</Row>
			<Row class="items-center justify-between h-10">
				<Label for="autoWomenRatio" class="w-3/5 text-left">
					Répartition automatique
					<TooltipProvider :delay-duration="200">
						<Tooltip>
							<TooltipTrigger>
								<Info class="size-4" />
							</TooltipTrigger>
							<TooltipContent>
								<p class="max-w-96">
									Si cette option est activée, le nombre de femmes par équipe sera déterminé de manière à ce qu'il soit égal pour chaque équipe, dans la mesure du possible.
								</p>
							</TooltipContent>
						</Tooltip>
					</TooltipProvider>
				</Label>
				<Switch id="autoWomenRatio" :checked="autoWomenRatio" @update:checked="value => autoWomenRatio = value" />
			</Row>
		</Column>
		<ErrorText v-if="error" class="mb-2">Une erreur est survenue.</ErrorText>

		<template #footer>
			<DialogClose>
				<Button variant="outline">Annuler</Button>
			</DialogClose>
			<LoadingButton type="submit" class="flex items-center" :loading="isPending" @click="mutate">
				Générer les équipes
			</LoadingButton>
		</template>
	</CustomDialog>
</template>