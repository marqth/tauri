<script setup lang="ts">

import { SidebarTemplate } from "@/components/templates"
import { Header } from "@/components/molecules/header"
import { AddGradeScale, GradeScaleSection } from "@/components/organisms/grade-scales"
import { useQuery, useQueryClient } from "@tanstack/vue-query"
import { getAllUnimportedGradeTypes } from "@/services/grade-type"
import { hasPermission } from "@/services/user"
import { Column } from "../atoms/containers"
import { NotAuthorized } from "../organisms/errors"
import AddFirstgradeScale from "../organisms/grade-scales/AddFirstgradeScale.vue"

const queryClient = useQueryClient()

const { data: gradeTypes, refetch } = useQuery({
	queryKey: ["grade-types-with-scale"],
	queryFn: async() => {
		const gradeTypes = await getAllUnimportedGradeTypes()
		void queryClient.invalidateQueries({ queryKey: ["grade-types-without-scale"] })
		return gradeTypes.filter(g => g.scaleTXTBlob !== null && g.scaleTXTBlob !== undefined)
	}
})

</script>

<template>
	<SidebarTemplate>
		<Header title="Barèmes" />
		<Column v-if="hasPermission('MANAGE_GRADE_SCALE')" class="gap-4 h-full">
			<GradeScaleSection v-for="gradeType in gradeTypes" :key="gradeType.id" :gradeType="gradeType"
				@delete:grade-scale="refetch" />
			<AddFirstgradeScale v-if="gradeTypes?.length === 0" @add:grade-scale="refetch" />
			<AddGradeScale v-else @add:grade-scale="refetch" />
		</Column>
		<NotAuthorized v-else />
	</SidebarTemplate>
</template>