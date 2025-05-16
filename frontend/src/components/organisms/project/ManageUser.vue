<script setup lang="ts">
import { ref, computed, onMounted } from "vue"
import { hasPermission } from "@/services/user/user.service"
import UserAdd from "./../../organisms/project/UserAdd.vue"
import UserList from "./../../organisms/project/UserList.vue"
import { getAllUsers } from "@/services/user/user.service"
import { getAllRoles } from "@/services/role/role.service"
import { type User } from "@/types/user"
import { type Role, type RoleType } from "@/types/role"
import Column from "@/components/atoms/containers/Column.vue"

const users = ref<User[]>([])
const roles = ref<Role[]>([])
const usersLoading = ref(true)
const rolesLoading = ref(true)
const usersError = ref(false)
const rolesError = ref(false)

onMounted(async() => {
	refetch()
})

const refetch = async() => {
	try {
		users.value = await getAllUsers()
	} catch (error) {
		usersError.value = true
	} finally {
		usersLoading.value = false
		usersError.value = false
	}

	try {
		roles.value = await getAllRoles()
	} catch (error) {
		rolesError.value = true
	} finally {
		rolesLoading.value = false
		rolesError.value = false
	}
}


const combinedData = computed(() => {
	return users.value.map(user => ({
		...user,
		role: getListOfRoles(user)
	}))
})

const getListOfRoles = (user: User) => {
	const rolesOfUser: RoleType[] = []
	roles.value.forEach(role => {
		if (role.user.id === user.id) {
			rolesOfUser.push(role.type)
		}
	})
	return rolesOfUser
}

</script>

<template>
    <Column class="items-center border rounded-md bg-white px-5 py-4">
        <p v-if="usersLoading || rolesLoading">Chargement...</p>
        <p v-else-if="usersError || rolesError">Erreur lors du chargement des projets.</p>
        <Column v-else class="w-full gap-8">
            <UserAdd v-if="hasPermission('ADD_USER')" @add:user="refetch" />
            <UserList v-if="hasPermission('DELETE_USER')" :users="combinedData" :users-loading="usersLoading"
                :users-error="usersError" :roles-loading="rolesLoading" :roles-error="rolesError"
                @delete:user="refetch" />
        </Column>
    </Column>
</template>