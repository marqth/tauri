import { z } from "zod"
import { ProjectSchema } from "./project"

export const GradeTypeSchema = z.object({
	id: z.number(),
	name: z.string(),
	factor: z.coerce.number().nullable(),
	forGroup: z.boolean(),
	imported: z.boolean(),
	project: ProjectSchema,
	scaleTXTBlob: z.string().nullable().optional()
})
export type GradeType = z.infer<typeof GradeTypeSchema>

export const CreateGradeTypeSchema = GradeTypeSchema.omit({
	id: true
})
export type CreateGradeType = z.infer<typeof CreateGradeTypeSchema>

export const UpdateGradeTypeSchema = CreateGradeTypeSchema.partial()
export type UpdateGradeType = z.infer<typeof UpdateGradeTypeSchema>

export const GradeTypeNameSchema = z.enum([
	"Solution Technique",
	"Conformité au sprint",
	"Gestion de projet",
	"Contenu de la présentation",
	// "Support de présentation",
	"Performance globale de l'équipe",
	"Performance individuelle",
	"Moyenne"
])
export type GradeTypeName = z.infer<typeof GradeTypeNameSchema>

export const getGradeTypeDescription = (gradeTypeName: GradeTypeName): string => {
	switch (gradeTypeName) {
	case "Solution Technique":
		return "Vous devez évaluer chaque équipe sur la solution technique qui a été mise en œuvre."
	case "Conformité au sprint":
		return "Vous devez évaluer la conformité du sprint des équipes."
	case "Gestion de projet":
		return "Vous devez évaluer chaque équipe sur sa gestion du projet."
	case "Contenu de la présentation":
		return "Vous devez évaluer chaque équipe sur le contenu de sa présentation."
	// case "Support de présentation":
	// 	return "Vous devez évaluer chaque équipe sur son support de présentation."
	case "Performance globale de l'équipe":
		return "Vous devez évaluer chaque équipe sur sa présentation globale."
	case "Performance individuelle":
		return "Vous devez évaluer chaque étudiant sur sa performance individuelle lors de sa présentation."
	case "Moyenne":
		return "La moyenne de l'étudiant importée."
	}
}

export const getGradeFormula = (total: "TE" | "TB" | "TI" | "NF"): string[] => {
	switch (total) {

	case "TE":
		return [
			`\\footnotesize{\\color{#301F70}{
\\text{Total équipe} = \\frac {\\textbf{ST} + \\textbf{GP} + \\textbf{CS} + \\textbf{PR}} 4
}} \\\\`,
			`\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{ST} \\textit{: Solution Technique}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{GP} \\textit{: Gestion de Projet}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{CS} \\textit{: Conformité au Sprint}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{PR} \\textit{: Présentation}}}}`
		]

	case "TB":
		return [
			`\\footnotesize{\\color{#301F70}{
\\text{Total bonus} = \\textbf{BL} + \\textbf{BI}
}} \\\\`,
			`\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{BL} \\textit{: Bonus et malus limités}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{BI} \\textit{: Bonus et malus illimités}}}}`
		]

	case "TI":
		return [
			`\\footnotesize{\\color{#301F70}{
\\text{Total individuel} = \\frac {\\textbf{PG} + 2 \\times \\textbf{PI}} 3
}} \\\\`,
			`\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{PG} \\textit{: Performance globale}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{PI} \\textit{: Performance individuelle}}}}`
		]

	case "NF":
		return [
			`\\footnotesize{\\color{#301F70}{
\\text{Note finale} = 0,7 \\times (\\textbf{TE} + \\textbf{TB}) + 0,3 \\times \\textbf{TI}
}} \\\\`,
			`\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{TE} \\textit{: Total Équipe}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{TB} \\textit{: Total Bonus}}}} \\\\
\\scriptsize{\\textcolor{gray}{\\textit{\\textbf{TI} \\textit{: Total Individuel}}}}`
		]

	}
}