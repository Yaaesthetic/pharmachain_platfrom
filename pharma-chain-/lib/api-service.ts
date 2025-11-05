import { apiClient } from "./api-client"
import type { Admin, Manager, Driver, Client, Bordereau, DeliveryItem, Transfer, PaginatedResponse } from "@/types"

export const api = {
  // Admin APIs
  getAdmins: async (page = 0, size = 20): Promise<PaginatedResponse<Admin>> => {
    const response = await apiClient.getAdmins(page, size)
    return response
  },
  getAdmin: async (code: string): Promise<Admin | undefined> => {
    try {
      return await apiClient.getAdmin(code)
    } catch {
      return undefined
    }
  },
  createAdmin: async (data: Partial<Admin>): Promise<Admin> => {
    return await apiClient.createAdmin(data)
  },
  updateAdmin: async (code: string, data: Partial<Admin>): Promise<Admin> => {
    return await apiClient.updateAdmin(code, data)
  },
  deleteAdmin: async (code: string): Promise<void> => {
    return await apiClient.deleteAdmin(code)
  },

  // Manager APIs
  getManagers: async (page = 0, size = 20): Promise<PaginatedResponse<Manager>> => {
    const response = await apiClient.getManagers(page, size)
    return response
  },
  getManager: async (code: string): Promise<Manager | undefined> => {
    try {
      return await apiClient.getManager(code)
    } catch {
      return undefined
    }
  },
  createManager: async (data: Partial<Manager>): Promise<Manager> => {
    return await apiClient.createManager(data)
  },
  updateManager: async (code: string, data: Partial<Manager>): Promise<Manager> => {
    return await apiClient.updateManager(code, data)
  },
  deleteManager: async (code: string): Promise<void> => {
    return await apiClient.deleteManager(code)
  },
  getManagerDrivers: async (code: string): Promise<Driver[]> => {
    return await apiClient.getManagerDrivers(code)
  },
  getManagerClients: async (code: string): Promise<Client[]> => {
    return await apiClient.getManagerClients(code)
  },
  getManagerBordereaux: async (code: string): Promise<Bordereau[]> => {
    return await apiClient.getManagerBordereaux(code)
  },

  // Driver APIs
  getDrivers: async (page = 0, size = 20): Promise<PaginatedResponse<Driver>> => {
    const response = await apiClient.getDrivers(page, size)
    return response
  },
  getDriver: async (code: string): Promise<Driver | undefined> => {
    try {
      return await apiClient.getDriver(code)
    } catch {
      return undefined
    }
  },
  createDriver: async (data: Partial<Driver>): Promise<Driver> => {
    return await apiClient.createDriver(data)
  },
  updateDriver: async (code: string, data: Partial<Driver>): Promise<Driver> => {
    return await apiClient.updateDriver(code, data)
  },
  deleteDriver: async (code: string): Promise<void> => {
    return await apiClient.deleteDriver(code)
  },
  getDriverBordereaux: async (code: string): Promise<Bordereau[]> => {
    return await apiClient.getDriverBordereaux(code)
  },
  getDriverDeliveryItems: async (code: string): Promise<DeliveryItem[]> => {
    return await apiClient.getDriverDeliveryItems(code)
  },

  // Client APIs
  getClients: async (page = 0, size = 20): Promise<PaginatedResponse<Client>> => {
    const response = await apiClient.getClients(page, size)
    return response
  },
  getClient: async (clientCode: string): Promise<Client | undefined> => {
    try {
      return await apiClient.getClient(clientCode)
    } catch {
      return undefined
    }
  },
  createClient: async (data: Partial<Client>): Promise<Client> => {
    return await apiClient.createClient(data)
  },
  updateClient: async (clientCode: string, data: Partial<Client>): Promise<Client> => {
    return await apiClient.updateClient(clientCode, data)
  },
  deleteClient: async (clientCode: string): Promise<void> => {
    return await apiClient.deleteClient(clientCode)
  },
  getClientDeliveryItems: async (clientCode: string): Promise<DeliveryItem[]> => {
    return await apiClient.getClientDeliveryItems(clientCode)
  },

  // Bordereau APIs
  getBordereaux: async (page = 0, size = 20): Promise<PaginatedResponse<Bordereau>> => {
    const response = await apiClient.getBordereaux(page, size)
    return response
  },
  getBordereau: async (bordereauNumber: string): Promise<Bordereau | undefined> => {
    try {
      return await apiClient.getBordereau(bordereauNumber)
    } catch {
      return undefined
    }
  },
  scanBordereau: async (data: any): Promise<Bordereau> => {
    return await apiClient.scanBordereau(data)
  },
  updateBordereau: async (bordereauNumber: string, data: Partial<Bordereau>): Promise<Bordereau> => {
    return await apiClient.updateBordereau(bordereauNumber, data)
  },
  deleteBordereau: async (bordereauNumber: string): Promise<void> => {
    return await apiClient.deleteBordereau(bordereauNumber)
  },
  reassignBordereau: async (bordereauNumber: string, driverCode: string, managerCode: string): Promise<Bordereau> => {
    return await apiClient.reassignBordereau(bordereauNumber, driverCode, managerCode)
  },
  getBordereauDeliveryItems: async (bordereauNumber: string): Promise<DeliveryItem[]> => {
    return await apiClient.getBordereauDeliveryItems(bordereauNumber)
  },

  // Delivery Item APIs
  getDeliveryItems: async (page = 0, size = 20): Promise<PaginatedResponse<DeliveryItem>> => {
    const response = await apiClient.getDeliveryItems(page, size)
    return response
  },
  getDeliveryItem: async (blNumber: string): Promise<DeliveryItem | undefined> => {
    try {
      return await apiClient.getDeliveryItem(blNumber)
    } catch {
      return undefined
    }
  },
  updateDeliveryItem: async (blNumber: string, data: Partial<DeliveryItem>): Promise<DeliveryItem> => {
    return await apiClient.updateDeliveryItem(blNumber, data)
  },
  deleteDeliveryItem: async (blNumber: string): Promise<void> => {
    return await apiClient.deleteDeliveryItem(blNumber)
  },
  updateProof: async (blNumber: string, data: Partial<DeliveryItem>): Promise<DeliveryItem> => {
    return await apiClient.updateProof(blNumber, data)
  },

  // Transfer APIs
  getTransfers: async (page = 0, size = 20): Promise<PaginatedResponse<Transfer>> => {
    const response = await apiClient.getTransfers(page, size)
    return response
  },
  getTransfer: async (id: number): Promise<Transfer | undefined> => {
    try {
      return await apiClient.getTransfer(id)
    } catch {
      return undefined
    }
  },
  createTransfer: async (bordereauNumber: string, data: Partial<Transfer>): Promise<Transfer> => {
    return await apiClient.createTransfer(bordereauNumber, data)
  },
  updateTransfer: async (id: number, data: Partial<Transfer>): Promise<Transfer> => {
    return await apiClient.updateTransfer(id, data)
  },
  updateTransferStatus: async (id: number, status: string): Promise<Transfer> => {
    return await apiClient.updateTransferStatus(id, status)
  },
  deleteTransfer: async (id: number): Promise<void> => {
    return await apiClient.deleteTransfer(id)
  },
}

export const mockApi = api
