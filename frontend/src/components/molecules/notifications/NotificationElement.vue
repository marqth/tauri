<script setup lang="ts">

import { SheetClose } from "@/components/ui/sheet"
import InfoText from "../../atoms/texts/InfoText.vue"
import { Column, Row } from "@/components/atoms/containers"
import Subtitle from "../../atoms/texts/Subtitle.vue"
import { Check, ExternalLink } from "lucide-vue-next"
import { LinkButton, LoadingButton } from "@/components/molecules/buttons"

const emits = defineEmits(["read:notifications"])

defineProps<{
	title: string
	description: string
  link: string
}>()

const clickButton = () => {
	emits("read:notifications")
}

</script>

<template>
	<Column class="mb-5 mr-5 border rounded-lg p-6">
		<Row>
			<Column class="items-start justify-center flex-1 gap-1">
				<Subtitle>{{ title }}</Subtitle>
				<InfoText>{{ description }}</InfoText>
			</Column>
		</Row>
		<Row class="mt-2.5 justify-end">
			<SheetClose>
				<LinkButton variant="outline" :link="link">
					<ExternalLink />Voir
				</LinkButton>
			</SheetClose>
			<LoadingButton class="ml-4" variant="outline" @click="clickButton">
				<Check />
			</LoadingButton>
		</Row>
	</Column>
</template>