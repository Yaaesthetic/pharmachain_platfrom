// Admin types
export interface Admin {
  id: number
  code: string
  username: string
  email?: string
  isActive: boolean
  createdAt: string
}

// Manager types
export interface Manager {
  id: number
  code: string
  username: string
  secteurName: string
  phone: string
  address: string
  isActive: boolean
  createdAt: string
  assignedAdmin: Admin
}

// Driver types
export interface Driver {
  id: number
  code: string
  username: string
  licenseNumber: string
  phone: string
  isActive: boolean
  createdAt: string
  assignedManager: Manager
}

// Client types
export interface Client {
  clientCode: string
  name: string
  address: string
  phone: string
  coordinates: string
  secteur: Manager
  autoCreated: boolean
}

// Bordereau types
export interface Bordereau {
  bordereauNumber: string
  deliveryDate: string
  currentDriver: Driver
  secteur: Manager
  originalDriver: Driver
  status: "PENDING" | "IN_PROGRESS" | "COMPLETED" | "CANCELLED"
  scannedAt: string
  completedAt: string | null
  autoCreated: boolean
}

// Delivery Item types
export interface DeliveryItem {
  id: number
  blNumber: string
  bordereau: Bordereau
  client: Client
  nombreColis: number
  nombreSachets: number
  status: "PENDING" | "IN_TRANSIT" | "DELIVERED" | "FAILED"
  deliveredAt: string | null
  deliveryNotes: string | null
  recipientSignature: string | null
}

// Transfer types
export interface Transfer {
  id: number
  bordereau: Bordereau
  fromDriver: Driver
  toDriver: Driver
  transferredAt: string
  transferBarcode: string
  reason: string
  status: "PENDING" | "ACCEPTED" | "REJECTED"
}

// API Response types
export interface PaginatedResponse<T> {
  content: T[]
  totalPages: number
  totalElements: number
  last: boolean
}
