<script setup lang="ts">

import { Scale } from "lucide-vue-next"
import { Column } from "@/components/atoms/containers"
import { AddGradeScaleDialog } from "."

const emits = defineEmits(["add:grade-scale"])

const TITLE = "Cliquez ici pour ajouter un barème de notation"

</script>

<template>
    <div class="border transition-colors rounded-lg bg-white hover:bg-muted/50 flex justify-center flex-col items-stretch">
        <AddGradeScaleDialog @add:grade-scale="emits('add:grade-scale')">
            <Column class="items-center py-4 gap-2">
				<Scale class="size-12 stroke-1 text-dark-blue" />
                <p class="text-dark-blue text-sm">{{ TITLE }}</p>
            </Column>
        </AddGradeScaleDialog>
    </div>
</template>