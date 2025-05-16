import { createRouter, createWebHistory } from "vue-router"

const router = createRouter({
	// eslint-disable-next-line @typescript-eslint/no-unsafe-argument,@typescript-eslint/no-unsafe-member-access
	history: createWebHistory(import.meta.env.BASE_URL),
	routes: [
		{
			path: "/login",
			name: "login",
			component: () => import("@/components/pages/ConnectionPage.vue")
		},
		{
			path: "/",
			name: "dashboard",
			component: () => import("@/components/pages/DashboardPage.vue")
		},
		{
			path: "/teams",
			name: "teams",
			component: () => import("@/components/pages/TeamsPage.vue")
		},
		{
			path: "/my-team",
			name: "my-team",
			component: () => import("@/components/pages/MyTeamPage.vue")
		},
		{
			path: "/students",
			name: "students",
			component: () => import("@/components/pages/StudentsPage.vue")
		},
		{
			path: "/sprints",
			name: "sprints",
			component: () => import("@/components/pages/SprintsPage.vue")
		},
		{
			path: "/grades",
			name: "grades",
			component: () => import("@/components/pages/GradesPage.vue")
		},
		{
			path: "/rating",
			name: "rating",
			component: () => import("@/components/pages/RatingPage.vue")
		},
		{
			path: "/grade-scales",
			name: "grade-scales",
			component: () => import("@/components/pages/GradeScalesPage.vue")
		},
		{
			path: "/project",
			name: "project",
			component: () => import("@/components/pages/ProjectPage.vue")
		},
		{
			path: "/:pathMatch(.*)*",
			name: "not-found",
			component: () => import("@/components/pages/NotFoundPage.vue")
		},
		{
			path: "/test",
			name: "test",
			component: () => import("@/components/pages/Test.vue")
		}
	]
})

export default router