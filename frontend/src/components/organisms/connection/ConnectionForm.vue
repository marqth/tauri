<script setup lang="ts">

import { useForm } from "vee-validate"
import { toTypedSchema } from "@vee-validate/zod"

import { login } from "@/services/auth"

import { FormControl, FormField, FormItem, FormMessage } from "@/components/ui/form"
import { Input } from "@/components/ui/input"
import { AuthRequestSchema } from "@/types/auth-request"
import { redirect } from "@/utils/router"
import { CustomCard } from "@/components/molecules/card"
import { Label } from "@/components/ui/label"
import { useMutation } from "@tanstack/vue-query"
import type { z } from "zod"
import { LoadingButton } from "../../molecules/buttons"
import { Row } from "@/components/atoms/containers"

const formSchema = toTypedSchema(AuthRequestSchema)

const { handleSubmit, setErrors } = useForm({
	validationSchema: formSchema
})

const { mutate, isPending } = useMutation({ mutationKey: ["login"], mutationFn: async(values: z.infer<typeof AuthRequestSchema>) => {
	await login(values.login, values.password)
		.then(() => {
			redirect("/")
		})
		.catch(() => {
			setErrors({
				login: "L'email ou le mot de passe est invalide.",
				password: "L'email ou le mot de passe est invalide."
			})
		})
} })

const onSubmit = handleSubmit((values) => {
	void mutate(values)
})

const CARD_TITLE = "Connexion"
const CARD_DESCRIPTION = "Entrez votre adresse email et votre mot de passe pour vous connecter."

</script>

<template>
	<form class="space-y-4 min-h-[440px]" @submit.prevent="onSubmit">
		<CustomCard class="border shadow-none" :title="CARD_TITLE" :description="CARD_DESCRIPTION">

			<FormField v-slot="{ componentField }" name="login">
				<FormItem class="space-y-1">
					<Label>Addresse mail</Label>
					<FormControl>
						<Input type="email" placeholder="email" v-bind="componentField" />
					</FormControl>
					<FormMessage />
				</FormItem>
			</FormField>

			<FormField v-slot="{ componentField }" name="password">
				<FormItem class="mt-4 space-y-1">
					<Label>Mot de passe</Label>
					<FormControl>
						<Input type="password" placeholder="mot de passe" v-bind="componentField" />
					</FormControl>
					<FormMessage />
				</FormItem>
			</FormField>

			<template v-slot:footer>
				<Row class="justify-end">
					<LoadingButton type="submit" :loading="isPending">
						Connexion
					</LoadingButton>
				</Row>
			</template>
		</CustomCard>
	</form>
</template>