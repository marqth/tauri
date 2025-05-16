<script setup lang="ts">
import type { Flag } from "@/types/flag"
import { useQuery, useQueryClient } from "@tanstack/vue-query"
import { getValidationFlagsByFlagId } from "@/services/validationFlag"
import { Column, Row } from "@/components/atoms/containers"
import { Subtitle, Text } from "@/components/atoms/texts"
import { ArrowLeftRight, Check, X } from "lucide-vue-next"
import { Button } from "@/components/ui/button"
import { UserTooltip } from "@/components/molecules/tooltip"
import { Cookies } from "@/utils/cookie"
import { updateValidationFlag } from "@/services/validationFlag"
import { createToast } from "@/utils/toast"
import { ref } from "vue"
import { updateFlag } from "@/services/flag"
import type { User } from "@/types/user"
import { addNotification } from "@/services/notification"

const props = defineProps<{
  flag: Flag
  isPl: boolean
}>()

const client = useQueryClient()

const canValidate = ref(true)
const currentUserId = Cookies.getUserId()
const concernedUsers = ref<User[]>([])

const { data: validations, refetch: refetchValidationFlags } = useQuery({
	queryKey: ["validationsFlag", props.flag.id],
	queryFn: async() => {
		const vFlags = await getValidationFlagsByFlagId(props.flag.id)
		concernedUsers.value = vFlags.map(vFlag => vFlag.author)
		if (!props.isPl) {
			canValidate.value = vFlags.filter(vFlag => vFlag.author.id === Number(currentUserId))[0].confirmed === null
		}
		return vFlags
	}
})

const handleValidationFlag = async(confirmed: boolean) => {
	if (!props.isPl) {
		await updateValidationFlag(props.flag.id, Number(currentUserId), confirmed)
		refetchValidationFlags()
	} else {
		await updateFlag(props.flag.id, { ...props.flag, status: confirmed })
		for (const user of concernedUsers.value) {
			await addNotification(user.id, currentUserId, `La demande de changement d'équipe entre ${props.flag.firstStudent!.name} et ${props.flag.secondStudent!.name} a été ${confirmed ? "validée" : "refusée"}`)
				.then(() => queryClient.invalidateQueries({ queryKey: ["notifications"] }))
		}
		await client.invalidateQueries({ queryKey: ["flagForConcerned"] })
	}
	const text = confirmed ? "validée" : "refusée"
	createToast("Demande de changement " + text)
}
</script>

<template>
  <Column class="gap-4 border rounded-lg p-2 md:p-6 bg-white">
    <Row class="justify-between">
    <Column class="items-start">
      <Subtitle>Demande de modification d'équipe faite par {{ props.flag.author.name}}</Subtitle>
      <Text>Commentaire :</Text>
    </Column>
      <Row v-if="canValidate">
        <Button variant="outline" class="mr-1" @click="handleValidationFlag(false)">
          <X/>
        </Button>
        <Button variant="outline" @click="handleValidationFlag(true)">
          <Check/>
        </Button>
      </Row>
    </Row>
    <div class="border rounded-lg p-5 items-start w-full">
      <p class="overflow-auto break-words text-gray-500 text-sm">
        {{props.flag.description}}
      </p>
    </div>
    <Column class="flex items-center justify-center gap-1 flex-grow">
      <Row class="justify-between">
        <Text>{{ props.flag.firstStudent!.name }} - {{ props.flag.firstStudent!.team!.name}}</Text>
        <ArrowLeftRight class="mr-2 ml-2"/>
        <Text>{{ props.flag.secondStudent!.name }} - {{ props.flag.secondStudent!.team!.name}}</Text>
      </Row>
      <Row v-if="validations">
        <div v-for="flag in validations"  :key="flag.author.id" class="shadow-2xl -mr-2">
          <UserTooltip :authorName="flag.author.name"  :confirmed="flag.confirmed"/>
        </div>
      </Row>
    </Column>
  </Column>
</template>

<style scoped>

</style>