<script setup lang="ts">

import { LoadingButton } from "@/components/molecules/buttons"
import { Check, X } from "lucide-vue-next"
import { extractNames } from "@/utils/string"
import { updateFlag } from "@/services/flag"
import type { Flag } from "@/types/flag"
import { useQueryClient } from "@tanstack/vue-query"

const props = defineProps<{
  report: Flag
}>()

const client = useQueryClient()

const author = props.report.author
const comment = props.report.description
const id = props.report.id

const authorName = extractNames(author.name).lastName

const updateAFlag = async(isValid: boolean) => {
	await updateFlag(id, { ...props.report, status: isValid })
	await client.invalidateQueries({
		queryKey: ["flags"]
	})
}
</script>

<template>
  <div class="flex p-4 flex-col items-start space-y-2 border border-gray-300 bg-white rounded-md max-w-[460px]">
    <div class="text-black font-inter text-sm font-medium leading-none">
      {{ authorName }}
    </div>
    <div class="self-stretch text-gray-500 font-inter text-sm font-normal leading-5 overflow-auto break-words">
      {{ comment }}
    </div>
    <div class="flex flex-row ml-auto">
      <LoadingButton class="ml-4" variant="outline" @click="updateAFlag(true)">
      <Check />
    </LoadingButton>
      <LoadingButton class="ml-4" variant="outline" @click="updateAFlag(false)">
        <X />
      </LoadingButton>
    </div>
  </div>
</template>

<style scoped>

</style>