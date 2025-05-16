<script setup lang="ts">
import { Tooltip, TooltipContent, TooltipProvider, TooltipTrigger } from "@/components/ui/tooltip"
import { User } from "lucide-vue-next"

const props = defineProps<{
	confirmed: boolean | null,
	authorName: string
}>()

</script>

<template>
	<TooltipProvider :delay-duration="200" v-if="props.confirmed === null">
		<Tooltip>
			<TooltipTrigger>
				<div class="rounded-full bg-gray-400 p-0.5 border">
					<User class="text-white" />
				</div>
			</TooltipTrigger>
			<TooltipContent>
				<p>{{ props.authorName }}</p>
			</TooltipContent>
		</Tooltip>
	</TooltipProvider>
	<TooltipProvider :delay-duration="200" v-if="props.confirmed === false">
		<Tooltip>
			<TooltipTrigger>
				<div class="rounded-full bg-tauri-red p-0.5 border">
					<User class="text-white" />
				</div>
			</TooltipTrigger>
			<TooltipContent>
				<p>{{ props.authorName }}</p>
			</TooltipContent>
		</Tooltip>
	</TooltipProvider>
	<TooltipProvider :delay-duration="200" v-if="props.confirmed">
		<Tooltip>
			<TooltipTrigger>
				<div class="rounded-full bg-dark-blue p-0.5 border">
					<User class="text-white" />
				</div>
			</TooltipTrigger>
			<TooltipContent>
				<p>{{ props.authorName }}</p>
			</TooltipContent>
		</Tooltip>
	</TooltipProvider>
</template>

<style scoped></style>