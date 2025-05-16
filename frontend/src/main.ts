import "./assets/main.css"

import { createApp } from "vue"
import App from "./App.vue"
import router from "./router"
import { VueQueryPlugin, type VueQueryPluginOptions } from "@tanstack/vue-query"
import VueLatex from "vatex"

// eslint-disable-next-line @typescript-eslint/no-unsafe-argument
const app = createApp(App)

app.use(router)

app.use(VueLatex)

const vueQueryPluginOptions: VueQueryPluginOptions = {
	queryClientConfig: {
		defaultOptions: {
			queries: {
				refetchOnWindowFocus: false
			}
		}
	}
}
app.use(VueQueryPlugin, vueQueryPluginOptions)

app.mount("#app")