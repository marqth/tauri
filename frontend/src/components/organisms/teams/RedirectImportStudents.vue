<script setup lang="ts">

import { StudentsNotImported } from "@/components/atoms/illustration"
import { RedirectButton } from "@/components/molecules/buttons"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"
import { UploadStudentsDialog } from "../students"

const ACTION_TITLE = "Vous n'avez pas encore importé les étudiants"
const ACTION_DESCRIPTION = "Pour générer les équipes, il faut commencer par importer les étudiants."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<StudentsNotImported class="h-96" />
		<template #action>
			<UploadStudentsDialog>
				<RedirectButton link="/students" class="mt-8">
					Importer les étudiants
				</RedirectButton>
			</UploadStudentsDialog>
		</template>
	</PlaceholderSection>
</template>