# Frontend

## Recette de cuisine pour ajouter une US

### 1. Créer un service

Dans le dossier src/services, ajouter une fonction dans le fichier concerné pour faire la requête à l'API.

Pour cela, il faut utiliser la fonction apiQuery qui permet de faire tout le travail à notre place, on a juste à lui passer plusieurs arguments :
- le type de requête (GET, POST, PUT, PATCH, DELETE)
- la route de l'API
- les données à envoyer dans le body (pour les requêtes POST, PUT et PATCH)
- le schéma Zod de validation des données reçues

Exemple pour récupérer les notes :
```ts
export const getAllGrades = async() => {
	const response = await apiQuery({
		route: "grades",
		responseSchema: z.array(GradeSchema),
		method: "GET"
	})

	if (response.status === "error") {
		throw new Error(response.error)
	}

	return response.data
}
```

### 2. Créer un composant

Dans le dossier src/components, ajouter un composant (la plupart du temps c'est un organisme), et appeler d'autres molécules et atomes pour construire ce composant.

Exemple ici où on utilise Button (atome) et ActionSection (molécule) pour construire lce composant :
(On utilise aussi une dialog qui est un autre organisme)
```vue
<template>
	<ActionSection :title="ACTION_TITLE" :description="ACTION_DESCRIPTION">
		<GenerateTeamsDialog @generate:teams="emits('generate:teams')" :nb-students="nbStudents">
			<Button>Générer les équipes</Button>
		</GenerateTeamsDialog>
	</ActionSection>
</template>
```

### 3. Récupérer les données

Dans le composant concerné, utiliser la fonction useQuery pour appeler le service qu'on a crée à l'étape 1.
```ts
const { data: grades } = useQuery({ queryKey: ["grades", props.teamId], queryFn: getAllGrades })
```

On peut même récupérer isFetching et isLoading pour savoir si les données sont en train de se charger et ainsi afficher un loader.
Par exemple :
```ts
const { data: grades, isLoading, isFetching } = useQuery({ queryKey: ["grades", props.teamId], queryFn: getAllGrades })
```
Puis dans le template Vue :
```vue
<template>
    <PageSkeleton v-if="isLoading || isFetching" />
    <Composant v-else />
<template
```

### 4. Faire des modifications

Si on a besoin de modifier des données, on peut utiliser la fonction useMutation qui permet de faire une requête POST, PUT ou PATCH.
```ts
const { mutate, isPending, error } = useMutation({ mutationKey: ["generate-teams"], mutationFn: async() => {
        await generateTeams(nbTeams.value, womenPerTeam.value)
} })
```

Puis on appelle la fonction mutate dans le bouton :
```vue
<Button @click="mutate">Générer les équipes</Button>
```