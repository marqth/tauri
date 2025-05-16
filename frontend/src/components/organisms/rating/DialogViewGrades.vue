<script setup lang="ts">

import { DialogClose } from "@/components/ui/dialog"
import { ErrorText } from "@/components/atoms/texts"
import { CustomDialog } from "@/components/molecules/dialog"
import { Button } from "@/components/ui/button"

defineProps<{
	title: string,
	description : string
}>()

// ALLER CHERCHER LES NOTES MISES PAR L AUTEUR COURANT

</script>

<template>
	<CustomDialog :title=title :description=description>
		<template #trigger>
			<Button variant="outline">Voir les notes</Button>
		</template>

		<ErrorText v-if="error" class="mb-2">Une erreur est survenue.</ErrorText>

		<div class="w-1/2 mx-auto py-4">
			<Table>
				<TableHeader>
					<TableRow>
						<TableHead class="w-[200px]">
							Nom de l'équipe
						</TableHead>
						<TableHead>Note</TableHead>
					</TableRow>
				</TableHeader>
				<TableBody>
<!--					<TableRow v-for="(teamEvaluations, teamName) in evaluations" :key="teamName">-->
<!--						<TableCell>-->
<!--							{{ teamName }}-->
<!--						</TableCell>-->
<!--						<TableCell v-for="(evaluation, index) in teamEvaluations" :key="index">-->
<!--							{{ evaluation.gradeOverallPerformance }}-->
<!--						</TableCell>-->
<!--						<TableCell class="text-right">-->
<!--							<DropdownMenu>-->
<!--								<DropdownMenuTrigger>-->
<!--									<Ellipsis/>-->
<!--								</DropdownMenuTrigger>-->
<!--								<DropdownMenuContent>-->
<!--									<DropdownMenuItem>-->
<!--										<Pencil/>-->
<!--										Modifier-->
<!--									</DropdownMenuItem>-->
<!--									<DropdownMenuItem>-->
<!--										<Trash2/>-->
<!--										Supprimer-->
<!--									</DropdownMenuItem>-->
<!--								</DropdownMenuContent>-->
<!--							</DropdownMenu>-->
<!--						</TableCell>-->
<!--					</TableRow>-->
				</TableBody>
			</Table>
		</div>
		<template #footer>
			<DialogClose>
				<Button variant="outline">Quitter</Button>
			</DialogClose>
		</template>
	</CustomDialog>
</template>

<style scoped>

</style>