<script setup lang="ts">
import { ref, watch } from "vue"
import { Column } from "@/components/atoms/containers"
import { Input } from "@/components/ui/input"
import { Button } from "@/components/ui/button"
import { RoleTypeSchema, type RoleType, formatRole } from "@/types/role"
import { createUser } from "@/services/user/user.service"
import { createRole } from "@/services/role/role.service"
import { useMutation } from "@tanstack/vue-query"
import { createToast } from "@/utils/toast"
import { type CreateUser } from "@/types/user"
import { h } from 'vue'
import { useForm } from 'vee-validate'
import { toTypedSchema } from '@vee-validate/zod'
import * as z from 'zod'

import {
    FormControl,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from '@/components/ui/form'
import { Checkbox } from '@/components/ui/checkbox'


const userRoles = ref<RoleType[]>([]);
const userMail = ref<string>("");
const userName = ref<string>("");
const createdUser = ref<CreateUser>();

const emits = defineEmits(["add:user"])


watch(userMail, (newMail) => {
    if (newMail) {
        const [firstName, lastNameWithDomain] = newMail.split('@')[0].split('.');
        if (firstName && lastNameWithDomain) {
            const lastName = lastNameWithDomain.toUpperCase();
            const formattedFirstName = firstName.charAt(0).toUpperCase() + firstName.slice(1);
            userName.value = `${lastName} ${formattedFirstName}`;
        } else {
            userName.value = "";
        }
    }
});


const { error, mutate } = useMutation({
    mutationKey: ["add-new-user"],
    mutationFn: async () => {

        if (!userMail.value || !userRoles.value || !userName.value) {
            alert("Veuillez remplir tous les champs");
            return
        }

        createdUser.value = { name: userName.value, email: userMail.value, password: "", privateKey: "" }

        await createUser(createdUser.value)
            .then(() => {
                createRole(userMail.value, userRoles.value)
                    .then(() => {
                        createToast("L'utilisateur a été ajouté avec son/ses rôle(s)")
                        userMail.value = "";
                        userName.value = "";
                        userRoles.value = [];
                        emits('add:user')
                    })
                    .catch(() => createToast("Erreur lors de la création du/des role(s)"))
            })
            .catch(() => {
                console.log("Error creating user")    
                createToast("Erreur lors de la création d'un utilisateur. Il se peut que l'email soit déjà enregistrée")
            })
    }
})


const formSchema = toTypedSchema(z.object({
    roles: z.array(z.string()).refine(value => value.some(role => role), {
        message: 'Vous devez choisir au moins 1 role.',
    }),
}))

const { handleSubmit } = useForm({
    validationSchema: formSchema,
    initialValues: {
        roles: [],
    },
})

const onSubmit = handleSubmit((values) => {
    userRoles.value = values.roles as RoleType[];
    mutate()
})

</script>

<template>
    <div
        class="border border-gray-300 border-dashed rounded-lg flex justify-center flex-col items-stretch p-4">
        <h2 class="text-xl font-semibold text-center mb-4">Ajouter un utilisateur</h2>
        <Column class="items-center gap-4">

            <div class="w-full flex flex-col items-start mb-2">
                <label for="email" class="mb-1 text-sm font-medium text-gray-700">Email</label>
                <Input id="email"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    type="text" v-model="userMail" />
            </div>

            <div class="w-full flex flex-col items-start mb-2">
                <label for="username" class="mb-1 text-sm font-medium text-gray-700">Nom d'utilisateur</label>
                <Input id="username"
                    class="w-full px-3 py-2 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                    type="text" v-model="userName" />
            </div>

            <form @submit.prevent="onSubmit" class="flex flex-col">  
                    <FormField name="items">
                        <FormItem>
                            <div class="mb-4">
                                <FormLabel class="text-base">
                                    Role(s) pour le nouvel utilisateur
                                </FormLabel>
                            </div>
                            <FormField v-for="roleType in RoleTypeSchema.options" v-slot="{ value, handleChange }" :key="roleType" type="checkbox" :value="roleType" :unchecked-value="false" name="roles">
                                <FormItem v-if="roleType != 'OPTION_STUDENT'" class="flex flex-row items-start space-x-3 space-y-0">
                                    <FormControl>
                                        <Checkbox :checked="value.includes(roleType)" @update:checked="handleChange" />
                                    </FormControl>
                                    <FormLabel class="font-normal">
                                        {{ formatRole(roleType) }}
                                    </FormLabel>
                                </FormItem>
                            </FormField>
                            <FormMessage />
                        </FormItem>
                    </FormField>
                <Button class="mt-2">Valider</Button>
                <p v-if=error>Erreur lors de la création d'un utilisateur</p>
            </form>
        </Column>
    </div>
</template>
