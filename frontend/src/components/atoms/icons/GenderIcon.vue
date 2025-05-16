<script setup lang="ts">

import type { Gender } from "@/types/student"
import { cn } from "@/utils/style"
import { ManIcon, WomanIcon } from "."

const props = defineProps<{
	gender: Gender
}>()

const style = cn(
	"w-4 h-4 fill-none",
	props.gender === "MAN" ? "stroke-yellow-600" : "stroke-blue-700"
)

</script>

<template>
	<ManIcon v-if="props.gender === 'MAN'" :class="style" />
	<WomanIcon v-else-if="props.gender === 'WOMAN'" :class="style" />
	<div v-else ></div>
</template>