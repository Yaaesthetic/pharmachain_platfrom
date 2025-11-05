"use client"

import { useState, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { apiClient } from "@/lib/api-client"
import type { Manager, Driver, Client, Bordereau } from "@/types"
import { getStatusColor } from "@/lib/utils-pharma"
import { Loader2, AlertCircle } from "lucide-react"

export const ManagerManagement = () => {
  const [managers, setManagers] = useState<Manager[]>([])
  const [selectedManager, setSelectedManager] = useState<Manager | null>(null)
  const [managerDrivers, setManagerDrivers] = useState<Driver[]>([])
  const [managerClients, setManagerClients] = useState<Client[]>([])
  const [managerBordereaux, setManagerBordereaux] = useState<Bordereau[]>([])
  const [filterSecteur, setFilterSecteur] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadManagers()
  }, [])

  const loadManagers = async () => {
    try {
      setError(null)
      setIsLoading(true)
      const data = await apiClient.getManagers()
      setManagers(data.content)
    } catch (err: any) {
      console.error("Failed to load managers:", err)
      setError(err.message || "Failed to load managers")
    } finally {
      setIsLoading(false)
    }
  }

  const loadManagerDetails = async (code: string) => {
    try {
      setError(null)
      setIsLoading(true)
      const drivers = await apiClient.getManagerDrivers(code)
      const clients = await apiClient.getManagerClients(code)
      const bordereaux = await apiClient.getManagerBordereaux(code)
      setManagerDrivers(drivers)
      setManagerClients(clients)
      setManagerBordereaux(bordereaux)
    } catch (err: any) {
      console.error("Failed to load manager details:", err)
      setError(err.message || "Failed to load manager details")
    } finally {
      setIsLoading(false)
    }
  }

  const handleDelete = async (code: string) => {
    if (window.confirm("Are you sure you want to delete this manager?")) {
      try {
        setError(null)
        await apiClient.deleteManager(code)
        await loadManagers()
        alert("Manager deleted successfully!")
      } catch (err: any) {
        console.error("Failed to delete manager:", err)
        setError(err.message || "Failed to delete manager")
      }
    }
  }

  const filteredManagers = managers.filter(
    (manager) => !filterSecteur || manager.secteurName.toLowerCase().includes(filterSecteur.toLowerCase()),
  )

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex justify-between items-center">
          <span>Manager Management</span>
        </CardTitle>
      </CardHeader>
      <CardContent>
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        <div className="mb-4">
          <Input
            placeholder="Filter by secteur name..."
            value={filterSecteur}
            onChange={(e) => setFilterSecteur(e.target.value)}
          />
        </div>
        {isLoading && filteredManagers.length === 0 ? (
          <div className="flex items-center justify-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-blue-600" />
            <span className="ml-2 text-gray-600">Loading managers...</span>
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Code</TableHead>
                <TableHead>Username</TableHead>
                <TableHead>Secteur</TableHead>
                <TableHead>Phone</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredManagers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={6} className="text-center py-4 text-gray-500">
                    No managers found
                  </TableCell>
                </TableRow>
              ) : (
                filteredManagers.map((manager) => (
                  <TableRow key={manager.id}>
                    <TableCell>{manager.code}</TableCell>
                    <TableCell>{manager.username}</TableCell>
                    <TableCell>{manager.secteurName}</TableCell>
                    <TableCell>{manager.phone}</TableCell>
                    <TableCell>
                      <Badge className={manager.isActive ? "bg-green-500" : "bg-red-500"}>
                        {manager.isActive ? "Active" : "Inactive"}
                      </Badge>
                    </TableCell>
                    <TableCell className="flex gap-2">
                      <Button
                        size="sm"
                        onClick={() => {
                          setSelectedManager(manager)
                          loadManagerDetails(manager.code)
                        }}
                      >
                        View Details
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => handleDelete(manager.code)}>
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        )}

        {selectedManager && (
          <div className="mt-6 space-y-4">
            <h3 className="text-lg font-bold">Manager: {selectedManager.secteurName}</h3>

            <div className="grid grid-cols-3 gap-4">
              <Card>
                <CardHeader>
                  <CardTitle className="text-sm">Drivers ({managerDrivers.length})</CardTitle>
                </CardHeader>
                <CardContent>
                  {managerDrivers.length === 0 ? (
                    <p className="text-sm text-gray-500">No drivers assigned</p>
                  ) : (
                    managerDrivers.map((driver) => (
                      <div key={driver.code} className="text-sm py-1">
                        {driver.username} ({driver.code})
                      </div>
                    ))
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-sm">Clients ({managerClients.length})</CardTitle>
                </CardHeader>
                <CardContent>
                  {managerClients.length === 0 ? (
                    <p className="text-sm text-gray-500">No clients assigned</p>
                  ) : (
                    managerClients.map((client) => (
                      <div key={client.clientCode} className="text-sm py-1">
                        {client.name}
                      </div>
                    ))
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="text-sm">Bordereaux ({managerBordereaux.length})</CardTitle>
                </CardHeader>
                <CardContent>
                  {managerBordereaux.length === 0 ? (
                    <p className="text-sm text-gray-500">No bordereaux assigned</p>
                  ) : (
                    managerBordereaux.map((bordereau) => (
                      <div key={bordereau.bordereauNumber} className="text-sm py-1">
                        {bordereau.bordereauNumber} -
                        <Badge className={`ml-2 ${getStatusColor(bordereau.status)}`}>{bordereau.status}</Badge>
                      </div>
                    ))
                  )}
                </CardContent>
              </Card>
            </div>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
