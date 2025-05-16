import { useToast } from "@/components/ui/toast"

export const createToast = (message: string) => {
	const { toast } = useToast()

	toast({
		description: message
	})
}