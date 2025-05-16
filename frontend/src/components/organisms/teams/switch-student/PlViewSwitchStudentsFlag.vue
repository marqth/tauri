<script setup lang="ts">
import SwitchStudentsFlag from "@/components/organisms/teams/switch-student/SwitchStudentsFlag.vue"
import type { Flag } from "@/types/flag"
import { CustomDialog } from "@/components/molecules/dialog"
import { ScrollArea } from "@/components/ui/scroll-area"

const props = defineProps<{
  flags: Flag[]
  isPl: boolean
}>()

const DIALOG_TITLE = "Demandes de changement"
</script>

<template>
  <CustomDialog :title="DIALOG_TITLE" description="">
    <template #trigger>
      <slot />
    </template>
    <ScrollArea class="h-[500px] w-full p-4">
      <div v-for="flag in props.flags" :key="flag.id">
        <SwitchStudentsFlag v-if="flag.status === null" :flag="flag" :isPl="props.isPl" class="mb-2"/>
      </div>
    </ScrollArea>
  </CustomDialog>
</template>

<style scoped>

</style>