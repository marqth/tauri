<script setup lang="ts">
import Report from "@/components/organisms/teams/see-reports-dialog/Report.vue"
import type { Flag } from "@/types/flag"
import { Text } from "@/components/atoms/texts"
import { ScrollArea } from "@/components/ui/scroll-area"

const props = defineProps<{
	reports: Flag[]
}>()

</script>

<template>
	<Text class="text-lg font-bold">Signalements</Text>
	<Text v-if="props.reports.length === 0" class="text-center">Aucun signalement</Text>
	<ScrollArea v-else class="max-h-[400px]">
		<div class="flex flex-col space-y-2">
			<div v-for="report in props.reports" :key="report.id">
				<Report v-if="report.status === null" :report="report" />
			</div>
		</div>
	</ScrollArea>
</template>

<style scoped></style>