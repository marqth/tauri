<script setup lang="ts">

import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table"
import type { Team } from "@/types/team"
import { getStudentsByTeamId } from "@/services/student"
import { useQuery } from "@tanstack/vue-query"
import { Column } from "@/components/atoms/containers"
import { cn } from "@/utils/style"
import { extractNames } from "@/utils/string"
import { CheckIcon, GenderIcon } from "@/components/atoms/icons"
import { Skeleton } from "@/components/ui/skeleton"

const props = defineProps<{
	team: Team
}>()

const { data: students, isLoading } = useQuery({ queryKey: ["team-students", props.team.id], queryFn: () => getStudentsByTeamId(props.team.id, false) })

const rowClass = cn("py-2 h-auto")

</script>

<template>
	<Column class="gap-2 mt-2">
		<p class="font-medium">
			{{ props.team.name }}
			{{ props.team.leader?.name ? `(${props.team.leader.name})` : "" }}
		</p>
		<div  class="border bg-white rounded-md">
			<Table class="flex-1">
				<TableHeader>
					<TableRow>
						<TableHead :class="rowClass" class="min-w-28">Nom</TableHead>
						<TableHead :class="rowClass" class="min-w-28">Prénom</TableHead>
						<TableHead :class="rowClass" class="min-w-16">Genre</TableHead>
						<TableHead :class="rowClass" class="min-w-16">Bachelor</TableHead>
					</TableRow>
				</TableHeader>

				<TableBody v-if="students">
					<TableRow v-for="student in students" :key="student.id">
						<TableCell :class="rowClass">{{ extractNames(student.name).lastName }}</TableCell>
						<TableCell :class="rowClass">{{ extractNames(student.name).firstName }}</TableCell>
						<TableCell :class="rowClass">
							<GenderIcon :gender="student.gender" />
						</TableCell>
						<TableCell :class="rowClass">
							<CheckIcon :checked="student.bachelor ?? false" />
						</TableCell>
					</TableRow>
				</TableBody>

				<TableBody v-else-if="isLoading">
					<TableRow v-for="i in 8" :key="i">
						<TableCell :class="rowClass"> <Skeleton class="w-5/6 h-5" /> </TableCell>
						<TableCell :class="rowClass"> <Skeleton class="w-5/6 h-5" /> </TableCell>
						<TableCell :class="rowClass"> <Skeleton class="w-5/6 h-5" /> </TableCell>
						<TableCell :class="rowClass"> <Skeleton class="w-5/6 h-5" /> </TableCell>
					</TableRow>
				</TableBody>
			</Table>
		</div>
	</Column>
</template>