/* eslint-env node */
// require("@rushstack/eslint-patch/modern-module-resolution")

module.exports = {
	root: true,
	"extends": [
		"plugin:vue/vue3-essential",
		"eslint:recommended",
		"@vue/eslint-config-typescript",
		"@maxencebonamy",
	],
	parser: "vue-eslint-parser",
	parserOptions: {
		"parser": "@typescript-eslint/parser",
		"project": false,
		"extraFileExtensions": [".vue"],
		"ecmaVersion": 2020,
		"sourceType": "module"
	},
	plugins: [
		"@typescript-eslint"
	],
	ignorePatterns: [
		"src/components/ui/**/*.vue",
		"src/components/ui/**/*.ts",
		"src/components/atoms/illustration/*.vue",
		"tailwind.config.js",
		"postcss.config.js",
		"vite.config.js"
	],
	rules: {
		"vue/multi-word-component-names": "off",
		"vue/no-reserved-component-names": "off",
		"@typescript-eslint/explicit-function-return-type": "off",
		"func-style": ["error", "expression", { "allowArrowFunctions": true }],
		"max-len": "off"
	},
	overrides: [
		{
			extends: ['plugin:@typescript-eslint/disable-type-checked'],
			files: ['./**/*.js', './**/*.jsx', './**/*.ts', './**/*.tsx', './**/*.vue'],
		},
	],
}
