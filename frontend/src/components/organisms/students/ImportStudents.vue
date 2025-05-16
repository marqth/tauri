<script setup lang="ts">

import { UploadStudentsDialog } from "."
import { Button } from "@/components/ui/button"
import { PlaceholderSection } from "@/components/molecules/placeholder-section"
import { StudentsNotImported } from "@/components/atoms/illustration"

const emits = defineEmits(["import:students"])

const ACTION_TITLE = "Vous n'avez pas encore importé les étudiants"
const ACTION_DESCRIPTION = "Pour importer les étudiants, il suffit de cliquer sur le bouton."

</script>

<template>
	<PlaceholderSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<StudentsNotImported class="h-96" />
		<template #action>
			<UploadStudentsDialog @import:students="emits('import:students')">
				<Button class="mt-8">Importer les étudiants</Button>
			</UploadStudentsDialog>
		</template>
	</PlaceholderSection>
</template>