<script setup lang="ts">

import type { Flag } from "@/types/flag"
import { useQuery } from "@tanstack/vue-query"
import { getUsersByRole } from "@/services/user"
import type { User } from "@/types/user"
import { Text } from "@/components/atoms/texts"
import { Progress } from "@/components/ui/progress"
import { ref } from "vue"

const props = defineProps<{
	validations: Flag[]
}>()

const nbSS = ref(0)
const nbValidate = ref(0)

const { data: validationRate } = useQuery({
	queryKey: ["ssNumber"], queryFn: async() => {
		const usersSS = await getUsersByRole("SUPERVISING_STAFF")
		nbSS.value = usersSS.length
		nbValidate.value = hasValidate(props.validations, usersSS)
		const validationRate = (nbValidate.value / nbSS.value) * 100
		return validationRate
	}
})

const hasValidate = (validations: Flag[], usersSS: User[]) => {
	const ssIds = usersSS.map(user => user.id)
	const validated = validations.filter(validation => ssIds.includes(validation.author.id))
	return validated.length
}

</script>

<template>
	<Text class="text-lg font-bold">Validation</Text>
	<Text>
		La composition des équipes a été validée par {{ nbValidate }} référents sur {{ nbSS }} ({{ validationRate }}%).
	</Text>
	<Progress v-model="validationRate" />
</template>