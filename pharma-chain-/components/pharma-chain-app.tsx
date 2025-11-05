"use client"

import { useState, useMemo } from "react"
import { useAuth } from "@/lib/auth-context"
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs"
import { AdminManagement } from "./admin-management"
import { ManagerManagement } from "./manager-management"
import { DriverManagement } from "./driver-management"
import { ClientManagement } from "./client-management"
import { BordereauManagement } from "./bordereau-management"
import { DeliveryItemManagement } from "./delivery-item-management"
import { TransferManagement } from "./transfer-management"
import { RoleGuard } from "./auth/role-guard"

export const PharmaChainApp = () => {
  const [activeTab, setActiveTab] = useState("admins")
  const { user } = useAuth()

  const adminRoles = useMemo(() => ["admin"], [])
  const managerRoles = useMemo(() => ["admin", "manager"], [])
  const driverRoles = useMemo(() => ["admin", "manager"], [])
  const clientRoles = useMemo(() => ["admin", "manager"], [])
  const borderRoles = useMemo(() => ["admin", "manager", "driver"], [])
  const deliveryRoles = useMemo(() => ["admin", "manager", "driver"], [])
  const transferRoles = useMemo(() => ["admin", "manager", "driver"], [])

  return (
    <div className="container mx-auto p-6">
      <div className="mb-6">
        <h1 className="text-3xl font-bold mb-2">PharmaChain Management System</h1>
        <p className="text-gray-600">
          Pharmaceutical Supply Chain Management
          <span className="ml-4 text-green-600 font-semibold">
            ● Connected with Keycloak as: {user?.username || "Unknown"}
          </span>
        </p>
      </div>

      <Tabs value={activeTab} onValueChange={setActiveTab}>
        <TabsList className="grid w-full grid-cols-7">
          <TabsTrigger value="admins">Admins</TabsTrigger>
          <TabsTrigger value="managers">Managers</TabsTrigger>
          <TabsTrigger value="drivers">Drivers</TabsTrigger>
          <TabsTrigger value="clients">Clients</TabsTrigger>
          <TabsTrigger value="bordereaux">Bordereaux</TabsTrigger>
          <TabsTrigger value="deliveryItems">Delivery Items</TabsTrigger>
          <TabsTrigger value="transfers">Transfers</TabsTrigger>
        </TabsList>

        <TabsContent value="admins">
          <RoleGuard allowedRoles={adminRoles}>
            <AdminManagement />
          </RoleGuard>
        </TabsContent>

        <TabsContent value="managers">
          <RoleGuard allowedRoles={managerRoles}>
            <ManagerManagement />
          </RoleGuard>
        </TabsContent>

        <TabsContent value="drivers">
          <RoleGuard allowedRoles={driverRoles}>
            <DriverManagement />
          </RoleGuard>
        </TabsContent>

        <TabsContent value="clients">
          <RoleGuard allowedRoles={clientRoles}>
            <ClientManagement />
          </RoleGuard>
        </TabsContent>

        <TabsContent value="bordereaux">
          <RoleGuard allowedRoles={borderRoles}>
            <BordereauManagement />
          </RoleGuard>
        </TabsContent>

        <TabsContent value="deliveryItems" className="mt-6">
          <RoleGuard allowedRoles={deliveryRoles}>
            <DeliveryItemManagement />
          </RoleGuard>
        </TabsContent>

        <TabsContent value="transfers" className="mt-6">
          <RoleGuard allowedRoles={transferRoles}>
            <TransferManagement />
          </RoleGuard>
        </TabsContent>
      </Tabs>
    </div>
  )
}
