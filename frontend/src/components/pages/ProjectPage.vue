<script setup lang="ts">
import { hasPermission } from "@/services/user/user.service"
import { NotAuthorized } from "@/components/organisms/errors"
import ManageProject from "../organisms/project/ManageProject.vue"
import ManageUser from "../organisms/project/ManageUser.vue"
import SidebarTemplate from "../templates/SidebarTemplate.vue"
import { Header } from "@/components/molecules/header"


</script>

<template>
    <SidebarTemplate>
		<Header title="Gestion de projet" />
        <ManageProject v-if="hasPermission('MANAGE_PROJECT')" />
        <ManageUser v-if="hasPermission('MANAGE_USER')" />
        <NotAuthorized v-if="!hasPermission('MANAGE_PROJECT') || !hasPermission('MANAGE_USER')" />
    </SidebarTemplate>
</template>