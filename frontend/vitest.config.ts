import { defineConfig } from "vitest/config"
import path from "path"

export default defineConfig({
	test: {
		exclude: [
			"node_modules/**",
			"coverage/**",
			"public/**",
			"src/assets/**",
			"src/components/**",
			"src/router/**"
		],
		alias: {
			"@": path.resolve(__dirname, "./src")
		}
	}
})