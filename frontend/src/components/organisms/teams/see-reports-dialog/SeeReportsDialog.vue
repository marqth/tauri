<script setup lang="ts">

import { CustomDialog } from "@/components/molecules/dialog"
import Reports from "@/components/organisms/teams/see-reports-dialog/Reports.vue"
import { useQuery } from "@tanstack/vue-query"
import { getAllFlags } from "@/services/flag"
import type { Flag } from "@/types/flag"
import { ref } from "vue"
import ValidationProgress from "@/components/organisms/teams/see-reports-dialog/ValidationProgress.vue"

const DIALOG_TITLE = "Avis"

const reportFlags = ref<Flag[]>([])
const validationFlags = ref<Flag[]>([])

useQuery({
	queryKey: ["flags"],
	queryFn: async() => {
		const flags = await getAllFlags()
		reportFlags.value = flags.filter(flag => flag.type === "REPORTING" && flag.status === null)
		validationFlags.value = flags.filter(flag => flag.type === "VALIDATION")
		return flags
	}
})

</script>

<template>
	<CustomDialog :title="DIALOG_TITLE" description="">
		<template #trigger>
			<slot />
		</template>
		<ValidationProgress :validations="validationFlags" />
		<Reports :reports="reportFlags" />
	</CustomDialog>
</template>