<script setup lang="ts">

import Input from "../../ui/input/Input.vue"
import { CloudUpload, X, Sheet } from "lucide-vue-next"
import { computed, ref } from "vue"
import { cn } from "@/utils/style"
import { Row } from "@/components/atoms/containers"
import { Text } from "@/components/atoms/texts"
import { Label } from "@/components/ui/label"
import Column from "@/components/atoms/containers/Column.vue"
import InfoText from "@/components/atoms/texts/InfoText.vue"

const props = defineProps<{
	extension: string
	class?: string
}>()

const file = defineModel<File | null>()
const dragging = ref(false)

const handleChangeFile = (event: Event) => {
	const inputElement = event.target as HTMLInputElement
	if (inputElement.files && inputElement.files[0]) {
		file.value = inputElement.files[0]
	}
}

const handleDropFile = (event: DragEvent) => {
	event.preventDefault()
	if (event.dataTransfer && event.dataTransfer.files) {
		if (event.dataTransfer.files[0].name.endsWith(`.${props.extension}`)) file.value = event.dataTransfer.files[0]
		dragging.value = false
	}
}

const handleDragEnter = (event: DragEvent) => {
	event.preventDefault()
	dragging.value = true
}

const handleDragOver = (event: DragEvent) => {
	event.preventDefault()
	if (event.dataTransfer) event.dataTransfer.dropEffect = "move"
	dragging.value = true
}

const handleDragLeave = (event: DragEvent) => {
	event.preventDefault()
	dragging.value = false
}

const uploadAreaStyle = computed(() => cn(
	"flex flex-col justify-center",
	"p-4",
	"text-center text-slate-500 font-normal",
	"rounded-md border transition-all cursor-pointer border-dashed",
	dragging.value ? "bg-slate-50" : " bg-white hover:bg-slate-50",
	props.class
))

</script>

<template>
	<Row v-if="file" class="gap-2 items-center justify-stretch px-2 py-1.5 my-4 whitespace-nowrap rounded-md bg-slate-100 text-slate-900" :class="props.class">
		<Sheet class="w-4 h-4" />
		<Text class="flex-1">{{ file?.name }}</Text>
		<X class="w-4 h-4 cursor-pointer" @click="() => file = null" />
	</Row>

	<Label v-else for="file-upload" :class="uploadAreaStyle">
		<Column
			class="items-center px-20 py-5 gap-1" @dragenter="handleDragEnter" @dragover="handleDragOver"
			@dragleave="handleDragLeave" @drop="handleDropFile"
		>
			<CloudUpload class="w-16 h-16 stroke-[1.2]" />
			<Text>Déposez un fichier ici ou cliquez ici pour sélectionnez un fichier</Text>
		</Column>
		<Input id="file-upload" type="file" @change="handleChangeFile" class="hidden" :accept="`.${extension}`" />
	</Label>
	<InfoText v-if="!file" class="text-slate-400 mb-4">Format accepté : .{{ extension }}</InfoText>
</template>