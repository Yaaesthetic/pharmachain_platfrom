const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL

interface ApiResponse<T> {
  content?: T[]
  data?: T
  message?: string
  error?: string
}

interface PaginatedApiResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  last: boolean
  number: number
  size: number
}

class ApiClient {
  private baseUrl: string

  constructor(baseUrl: string) {
    this.baseUrl = baseUrl
  }

  private getAuthHeaders(): HeadersInit {
    const token = typeof window !== "undefined" ? localStorage.getItem("accessToken") : null
    const headers: HeadersInit = {
      "Content-Type": "application/json",
    }

    if (token) {
      headers["Authorization"] = `Bearer ${token}`
    }

    return headers
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    const url = `${this.baseUrl}${endpoint}`
    const authHeaders = this.getAuthHeaders()
    const headers = {
      ...authHeaders,
      ...options.headers,
    }

    try {
      const response = await fetch(url, {
        ...options,
        headers,
      })

      if (response.status === 401) {
        if (typeof window !== "undefined") {
          localStorage.removeItem("accessToken")
          localStorage.removeItem("refreshToken")
          localStorage.removeItem("user")
          window.location.href = "/login"
        }
        throw new Error("Session expired - Please login again")
      }

      if (response.status === 403) {
        throw new Error("Forbidden - You don't have permission to access this resource")
      }

      if (!response.ok) {
        const errorData = await response.json().catch(() => ({}))
        throw new Error(errorData.message || `API Error: ${response.status}`)
      }

      return await response.json()
    } catch (error) {
      console.error(`[API Error] ${endpoint}:`, error)
      throw error
    }
  }

  // Admin endpoints
  async getAdmins(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/admins?page=${page}&size=${size}`)
  }

  async getAdmin(id: number) {
    return this.request<any>(`/admins/${id}`)
  }

  async getMyAdminInfo() {
    return this.request<any>("/admins/me")
  }

  async getMyAdminProfile() {
    return this.request<any>("/admins/me/profile")
  }

  async createAdmin(data: any) {
    return this.request<any>("/admins", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async updateAdmin(id: number, data: any) {
    return this.request<any>(`/admins/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async partialUpdateAdmin(id: number, data: any) {
    return this.request<any>(`/admins/${id}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    })
  }

  async deleteAdmin(id: number) {
    return this.request<void>(`/admins/${id}`, {
      method: "DELETE",
    })
  }

  // Manager endpoints
  async getManagers(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/managers?page=${page}&size=${size}`)
  }

  async getManager(code: string) {
    return this.request<any>(`/managers/${code}`)
  }

  async getMyManagerInfo() {
    return this.request<any>("/managers/me")
  }

  async getMyManagerProfile() {
    return this.request<any>("/managers/me/profile")
  }

  async createManager(data: any) {
    return this.request<any>("/managers", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async updateManager(code: string, data: any) {
    return this.request<any>(`/managers/${code}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async partialUpdateManager(code: string, data: any) {
    return this.request<any>(`/managers/${code}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    })
  }

  async deleteManager(code: string) {
    return this.request<void>(`/managers/${code}`, {
      method: "DELETE",
    })
  }

  async getMyManagerDrivers() {
    return this.request<any[]>("/managers/me/drivers")
  }

  async getMyManagerClients() {
    return this.request<any[]>("/managers/me/clients")
  }

  async getMyManagerBordereaux() {
    return this.request<any[]>("/managers/me/bordereaux")
  }

  async getManagerDrivers(code: string) {
    return this.request<any[]>(`/managers/${code}/drivers`)
  }

  async getManagerClients(code: string) {
    return this.request<any[]>(`/managers/${code}/clients`)
  }

  async getManagerBordereaux(code: string) {
    return this.request<any[]>(`/managers/${code}/bordereaux`)
  }

  // Driver endpoints
  async getDrivers(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/drivers?page=${page}&size=${size}`)
  }

  async getDriver(code: string) {
    return this.request<any>(`/drivers/${code}`)
  }

  async getMyDriverInfo() {
    return this.request<any>("/drivers/me")
  }

  async getMyDriverProfile() {
    return this.request<any>("/drivers/me/profile")
  }

  async createDriver(data: any) {
    return this.request<any>("/drivers", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async updateDriver(code: string, data: any) {
    return this.request<any>(`/drivers/${code}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async partialUpdateDriver(code: string, data: any) {
    return this.request<any>(`/drivers/${code}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    })
  }

  async deleteDriver(code: string) {
    return this.request<void>(`/drivers/${code}`, {
      method: "DELETE",
    })
  }

  async getMyDriverBordereaux() {
    return this.request<any[]>("/drivers/me/bordereaux")
  }

  async getMyDriverDeliveryItems() {
    return this.request<any[]>("/drivers/me/delivery-items")
  }

  async getDriverBordereaux(code: string) {
    return this.request<any[]>(`/drivers/${code}/bordereaux`)
  }

  async getDriverDeliveryItems(code: string) {
    return this.request<any[]>(`/drivers/${code}/delivery-items`)
  }

  // Client endpoints
  async getClients(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/clients?page=${page}&size=${size}`)
  }

  async getClient(clientCode: string) {
    return this.request<any>(`/clients/${clientCode}`)
  }

  async createClient(data: any) {
    return this.request<any>("/clients", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async updateClient(clientCode: string, data: any) {
    return this.request<any>(`/clients/${clientCode}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async partialUpdateClient(clientCode: string, data: any) {
    return this.request<any>(`/clients/${clientCode}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    })
  }

  async deleteClient(clientCode: string) {
    return this.request<void>(`/clients/${clientCode}`, {
      method: "DELETE",
    })
  }

  async getClientDeliveryItems(clientCode: string) {
    return this.request<any[]>(`/clients/${clientCode}/delivery-items`)
  }

  // Bordereau endpoints
  async getBordereaux(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/bordereaux?page=${page}&size=${size}`)
  }

  async getBordereau(bordereauNumber: string) {
    return this.request<any>(`/bordereaux/${bordereauNumber}`)
  }

  async scanBordereau(data: any) {
    return this.request<any>("/bordereaux/scan", {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async updateBordereau(bordereauNumber: string, data: any) {
    return this.request<any>(`/bordereaux/${bordereauNumber}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async partialUpdateBordereau(bordereauNumber: string, data: any) {
    return this.request<any>(`/bordereaux/${bordereauNumber}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    })
  }

  async deleteBordereau(bordereauNumber: string) {
    return this.request<void>(`/bordereaux/${bordereauNumber}`, {
      method: "DELETE",
    })
  }

  async reassignBordereau(bordereauNumber: string, driverCode: string, managerCode: string) {
    return this.request<any>(`/bordereaux/${bordereauNumber}/assignments`, {
      method: "PUT",
      body: JSON.stringify({ driverCode, managerCode }),
    })
  }

  async getBordereauDeliveryItems(bordereauNumber: string) {
    return this.request<any[]>(`/bordereaux/${bordereauNumber}/delivery-items`)
  }

  // Delivery Item endpoints
  async getDeliveryItems(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/delivery-items?page=${page}&size=${size}`)
  }

  async getDeliveryItem(blNumber: string) {
    return this.request<any>(`/delivery-items/${blNumber}`)
  }

  async updateDeliveryItem(blNumber: string, data: any) {
    return this.request<any>(`/delivery-items/${blNumber}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async partialUpdateDeliveryItem(blNumber: string, data: any) {
    return this.request<any>(`/delivery-items/${blNumber}`, {
      method: "PATCH",
      body: JSON.stringify(data),
    })
  }

  async deleteDeliveryItem(blNumber: string) {
    return this.request<void>(`/delivery-items/${blNumber}`, {
      method: "DELETE",
    })
  }

  async updateProof(blNumber: string, data: any) {
    return this.request<any>(`/delivery-items/${blNumber}/proof`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  // Transfer endpoints
  async getTransfers(page = 0, size = 20) {
    return this.request<PaginatedApiResponse<any>>(`/transfers?page=${page}&size=${size}`)
  }

  async getTransfer(id: number) {
    return this.request<any>(`/transfers/${id}`)
  }

  async createTransfer(bordereauNumber: string, data: any) {
    return this.request<any>(`/bordereaux/${bordereauNumber}/transfers`, {
      method: "POST",
      body: JSON.stringify(data),
    })
  }

  async updateTransfer(id: number, data: any) {
    return this.request<any>(`/transfers/${id}`, {
      method: "PUT",
      body: JSON.stringify(data),
    })
  }

  async updateTransferStatus(id: number, status: string) {
    return this.request<any>(`/transfers/${id}/status`, {
      method: "PATCH",
      body: JSON.stringify({ status }),
    })
  }

  async deleteTransfer(id: number) {
    return this.request<void>(`/transfers/${id}`, {
      method: "DELETE",
    })
  }
}

export const apiClient = new ApiClient(API_BASE_URL)
