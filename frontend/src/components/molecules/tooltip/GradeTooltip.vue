<script setup lang="ts">

import { Column } from "@/components/atoms/containers"
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { Info } from "lucide-vue-next"
import { VueLatex } from "vatex"

defineProps<{
	formula: string[]
}>()

</script>

<template>
	<TooltipProvider :delay-duration="200">
		<Tooltip>
			<TooltipTrigger>
				<Info class="size-4" />
			</TooltipTrigger>
			<TooltipContent>
				<Column class="pb-2 px-2 items-start gap-2">
					<VueLatex v-for="(expression, i) in formula" :expression="expression" :key="i" :display-mode="i === 0"/>
				</Column>
			</TooltipContent>
		</Tooltip>
	</TooltipProvider>
</template>