<script setup lang="ts">

import {
	DropdownMenu,
	DropdownMenuContent,
	DropdownMenuGroup,
	DropdownMenuItem,
	DropdownMenuTrigger
} from "@/components/ui/dropdown-menu"
import { Ellipsis, Trash2, PencilLine } from "lucide-vue-next"
import type { Student } from "@/types/student"
import type { Grade } from "@/types/grade"
import { EditStudentDialog } from "@/components/organisms/students"
import { Button } from "@/components/ui/button"

const props = defineProps<{
	student: Student
	mark: Grade
}>()

</script>

<template>
	<DropdownMenu>
		<DropdownMenuTrigger as-child>
			<div>
				<Ellipsis class="stroke-gray-800" />
			</div>
		</DropdownMenuTrigger>
		<DropdownMenuContent>
			<DropdownMenuGroup>
				<DropdownMenuItem>
					<PencilLine class="mr-2 h-4 w-4" />
					<EditStudentDialog :student="props.student" :mark="props.mark">
						<Button>Modifier</Button>
					</EditStudentDialog>
				</DropdownMenuItem>
				<DropdownMenuItem>
					<Trash2 class="mr-2 h-4 w-4" />
					<span>Supprimer</span>
				</DropdownMenuItem>
			</DropdownMenuGroup>
		</DropdownMenuContent>
	</DropdownMenu>
</template>