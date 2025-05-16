import type { Criteria } from "@/types/criteria"


export const fakeCriteria = (): Criteria => {
	return {
		nbWomens: 2,
		nbBachelors: 2,
		validCriteriaWoman: true,
		validCriteriaBachelor: false
	}
}