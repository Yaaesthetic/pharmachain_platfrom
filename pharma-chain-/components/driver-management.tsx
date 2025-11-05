"use client"

import { useState, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { apiClient } from "@/lib/api-client"
import type { Driver, Bordereau } from "@/types"
import { getStatusColor, formatDate } from "@/lib/utils-pharma"
import { Loader2, AlertCircle } from "lucide-react"

export const DriverManagement = () => {
  const [drivers, setDrivers] = useState<Driver[]>([])
  const [selectedDriver, setSelectedDriver] = useState<Driver | null>(null)
  const [driverBordereaux, setDriverBordereaux] = useState<Bordereau[]>([])
  const [filterCode, setFilterCode] = useState("")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadDrivers()
  }, [])

  const loadDrivers = async () => {
    try {
      setError(null)
      setIsLoading(true)
      const data = await apiClient.getDrivers()
      setDrivers(data.content)
    } catch (err: any) {
      console.error("Failed to load drivers:", err)
      setError(err.message || "Failed to load drivers")
    } finally {
      setIsLoading(false)
    }
  }

  const loadDriverDetails = async (code: string) => {
    try {
      setError(null)
      const bordereaux = await apiClient.getDriverBordereaux(code)
      setDriverBordereaux(bordereaux)
    } catch (err: any) {
      console.error("Failed to load driver bordereaux:", err)
      setError(err.message || "Failed to load driver bordereaux")
    }
  }

  const handleDelete = async (code: string) => {
    if (window.confirm("Are you sure you want to delete this driver?")) {
      try {
        setError(null)
        await apiClient.deleteDriver(code)
        await loadDrivers()
        alert("Driver deleted successfully!")
      } catch (err: any) {
        console.error("Failed to delete driver:", err)
        setError(err.message || "Failed to delete driver")
      }
    }
  }

  const filteredDrivers = drivers.filter((driver) => !filterCode || driver.code.includes(filterCode))

  return (
    <Card>
      <CardHeader>
        <CardTitle>Driver Management</CardTitle>
      </CardHeader>
      <CardContent>
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        <div className="mb-4">
          <Input placeholder="Filter by code..." value={filterCode} onChange={(e) => setFilterCode(e.target.value)} />
        </div>
        {isLoading && filteredDrivers.length === 0 ? (
          <div className="flex items-center justify-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-blue-600" />
            <span className="ml-2 text-gray-600">Loading drivers...</span>
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>Code</TableHead>
                <TableHead>Username</TableHead>
                <TableHead>License</TableHead>
                <TableHead>Phone</TableHead>
                <TableHead>Manager</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredDrivers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} className="text-center py-4 text-gray-500">
                    No drivers found
                  </TableCell>
                </TableRow>
              ) : (
                filteredDrivers.map((driver) => (
                  <TableRow key={driver.id}>
                    <TableCell>{driver.code}</TableCell>
                    <TableCell>{driver.username}</TableCell>
                    <TableCell>{driver.licenseNumber}</TableCell>
                    <TableCell>{driver.phone}</TableCell>
                    <TableCell>{driver.assignedManager?.secteurName || "N/A"}</TableCell>
                    <TableCell>
                      <Badge className={driver.isActive ? "bg-green-500" : "bg-red-500"}>
                        {driver.isActive ? "Active" : "Inactive"}
                      </Badge>
                    </TableCell>
                    <TableCell className="flex gap-2">
                      <Button
                        size="sm"
                        onClick={() => {
                          setSelectedDriver(driver)
                          loadDriverDetails(driver.code)
                        }}
                      >
                        View Bordereaux
                      </Button>
                      <Button size="sm" variant="destructive" onClick={() => handleDelete(driver.code)}>
                        Delete
                      </Button>
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        )}

        {selectedDriver && (
          <div className="mt-6">
            <h3 className="text-lg font-bold mb-4">Driver: {selectedDriver.username} - Bordereaux</h3>
            {driverBordereaux.length === 0 ? (
              <p className="text-gray-500">No bordereaux assigned to this driver</p>
            ) : (
              <Table>
                <TableHeader>
                  <TableRow>
                    <TableHead>Bordereau Number</TableHead>
                    <TableHead>Delivery Date</TableHead>
                    <TableHead>Status</TableHead>
                  </TableRow>
                </TableHeader>
                <TableBody>
                  {driverBordereaux.map((bordereau) => (
                    <TableRow key={bordereau.bordereauNumber}>
                      <TableCell>{bordereau.bordereauNumber}</TableCell>
                      <TableCell>{formatDate(bordereau.deliveryDate)}</TableCell>
                      <TableCell>
                        <Badge className={getStatusColor(bordereau.status)}>{bordereau.status}</Badge>
                      </TableCell>
                    </TableRow>
                  ))}
                </TableBody>
              </Table>
            )}
          </div>
        )}
      </CardContent>
    </Card>
  )
}
