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
    <div className="space-y-6">
      <div className="relative overflow-hidden rounded-3xl border border-slate-200/80 bg-white/80 shadow-xl backdrop-blur">
        <div className="pointer-events-none absolute inset-0">
          <div className="absolute inset-0 bg-gradient-to-r from-teal-50 via-white to-white opacity-90 dark:from-slate-900 dark:via-slate-900 dark:to-slate-900" />
          <div className="absolute right-0 top-0 h-48 w-48 rounded-full bg-teal-200/30 blur-3xl dark:bg-teal-900/20" />
        </div>
        <div className="relative space-y-4 px-6 py-7 md:px-10">
          <div className="flex flex-col gap-4 md:flex-row md:items-center md:justify-between">
            <div className="space-y-2">
              <p className="text-xs font-semibold uppercase tracking-[0.28em] text-teal-700">Control Center</p>
              <h1 className="text-3xl font-semibold text-slate-900 dark:text-white">PharmaChain Management</h1>
              <p className="text-slate-600 dark:text-slate-300">Oversee admins, teams, and deliveries with a focused, minimal workspace.</p>
            </div>
            <div className="flex items-center gap-3 rounded-full border border-teal-100 bg-teal-50 px-4 py-2 text-sm font-medium text-teal-900 shadow-sm ring-1 ring-teal-500/10 dark:border-teal-900 dark:bg-teal-900/40 dark:text-teal-100">
              <span className="h-2.5 w-2.5 rounded-full bg-teal-500 shadow-[0_0_0_4px_rgba(13,148,136,0.12)]" aria-hidden />
              Connected as {user?.username || "Unknown"}
            </div>
          </div>
          <div className="grid gap-3 text-sm text-slate-600 dark:text-slate-300 sm:grid-cols-2">
            <span className="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white/70 px-3 py-2 font-medium text-slate-800 shadow-sm dark:border-slate-800 dark:bg-slate-900/70 dark:text-white">
              <span className="h-2 w-2 rounded-full bg-slate-400" aria-hidden />
              Roles: {user?.roles?.join(", ") || "Not assigned"}
            </span>
            <span className="inline-flex items-center gap-2 rounded-xl border border-slate-200 bg-white/70 px-3 py-2 font-medium text-slate-800 shadow-sm dark:border-slate-800 dark:bg-slate-900/70 dark:text-white">
              <span className="h-2 w-2 rounded-full bg-slate-400" aria-hidden />
              ID: {user?.code || "—"}
            </span>
          </div>
        </div>
      </div>

      <div className="rounded-3xl border border-slate-200/80 bg-white/80 p-4 shadow-lg backdrop-blur dark:border-slate-800 dark:bg-slate-900/80">
        <Tabs value={activeTab} onValueChange={setActiveTab}>
          <TabsList className="grid w-full grid-cols-7 gap-2 rounded-2xl border border-slate-200 bg-slate-100/60 p-1 text-sm shadow-inner dark:border-slate-800 dark:bg-slate-800/60">
            <TabsTrigger value="admins" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Admins</TabsTrigger>
            <TabsTrigger value="managers" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Managers</TabsTrigger>
            <TabsTrigger value="drivers" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Drivers</TabsTrigger>
            <TabsTrigger value="clients" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Clients</TabsTrigger>
            <TabsTrigger value="bordereaux" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Bordereaux</TabsTrigger>
            <TabsTrigger value="deliveryItems" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Delivery Items</TabsTrigger>
            <TabsTrigger value="transfers" className="data-[state=active]:text-teal-900 dark:data-[state=active]:text-teal-100">Transfers</TabsTrigger>
          </TabsList>

          <div className="mt-4 space-y-6">
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

            <TabsContent value="deliveryItems" className="mt-0">
              <RoleGuard allowedRoles={deliveryRoles}>
                <DeliveryItemManagement />
              </RoleGuard>
            </TabsContent>

            <TabsContent value="transfers" className="mt-0">
              <RoleGuard allowedRoles={transferRoles}>
                <TransferManagement />
              </RoleGuard>
            </TabsContent>
          </div>
        </Tabs>
      </div>
    </div>
  )
}
