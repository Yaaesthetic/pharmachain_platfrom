export const getStatusColor = (status: string): string => {
  const colors: Record<string, string> = {
    PENDING: "bg-yellow-500",
    IN_PROGRESS: "bg-blue-500",
    IN_TRANSIT: "bg-blue-500",
    COMPLETED: "bg-green-500",
    DELIVERED: "bg-green-500",
    CANCELLED: "bg-red-500",
    FAILED: "bg-red-500",
    REQUESTED: "bg-orange-500",
    RETURNED: "bg-purple-500",
  }
  return colors[status] || "bg-gray-500"
}

export const formatDate = (dateString: string | null | undefined): string => {
  if (!dateString) return "N/A"
  return new Date(dateString).toLocaleString()
}
