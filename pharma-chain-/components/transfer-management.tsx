"use client"

import { useState, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { apiClient } from "@/lib/api-client"
import type { Transfer } from "@/types"
import { getStatusColor, formatDate } from "@/lib/utils-pharma"
import { Loader2, AlertCircle } from "lucide-react"

export const TransferManagement = () => {
  const [transfers, setTransfers] = useState<Transfer[]>([])
  const [filterStatus, setFilterStatus] = useState("all")
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    loadTransfers()
  }, [])

  const loadTransfers = async () => {
    try {
      setError(null)
      setIsLoading(true)
      const data = await apiClient.getTransfers()
      setTransfers(data.content)
    } catch (err: any) {
      console.error("Failed to load transfers:", err)
      setError(err.message || "Failed to load transfers")
    } finally {
      setIsLoading(false)
    }
  }

  const handleUpdateStatus = async (id: number, status: string) => {
    try {
      setError(null)
      await apiClient.updateTransferStatus(id, status)
      await loadTransfers()
      alert(`Transfer ${status.toLowerCase()} successfully!`)
    } catch (err: any) {
      console.error("Failed to update transfer status:", err)
      setError(err.message || "Failed to update transfer status")
    }
  }

  const filteredTransfers = transfers.filter((transfer) => filterStatus === "all" || transfer.status === filterStatus)

  return (
    <Card>
      <CardHeader>
        <CardTitle>Transfer Management</CardTitle>
      </CardHeader>
      <CardContent>
        {error && (
          <Alert variant="destructive" className="mb-4">
            <AlertCircle className="h-4 w-4" />
            <AlertDescription>{error}</AlertDescription>
          </Alert>
        )}
        <div className="mb-4">
          <Select value={filterStatus} onValueChange={setFilterStatus}>
            <SelectTrigger>
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Status</SelectItem>
              <SelectItem value="PENDING">Pending</SelectItem>
              <SelectItem value="ACCEPTED">Accepted</SelectItem>
              <SelectItem value="REJECTED">Rejected</SelectItem>
            </SelectContent>
          </Select>
        </div>
        {isLoading && filteredTransfers.length === 0 ? (
          <div className="flex items-center justify-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-blue-600" />
            <span className="ml-2 text-gray-600">Loading transfers...</span>
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Bordereau</TableHead>
                <TableHead>From Driver</TableHead>
                <TableHead>To Driver</TableHead>
                <TableHead>Reason</TableHead>
                <TableHead>Barcode</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Transferred At</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredTransfers.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={9} className="text-center py-4 text-gray-500">
                    No transfers found
                  </TableCell>
                </TableRow>
              ) : (
                filteredTransfers.map((transfer) => (
                  <TableRow key={transfer.id}>
                    <TableCell>{transfer.id}</TableCell>
                    <TableCell>{transfer.bordereau?.bordereauNumber || "N/A"}</TableCell>
                    <TableCell>{transfer.fromDriver?.username || "N/A"}</TableCell>
                    <TableCell>{transfer.toDriver?.username || "N/A"}</TableCell>
                    <TableCell>{transfer.reason}</TableCell>
                    <TableCell>{transfer.transferBarcode}</TableCell>
                    <TableCell>
                      <Badge className={getStatusColor(transfer.status)}>{transfer.status}</Badge>
                    </TableCell>
                    <TableCell>{formatDate(transfer.transferredAt)}</TableCell>
                    <TableCell>
                      {transfer.status === "PENDING" && (
                        <div className="flex gap-2">
                          <Button
                            size="sm"
                            variant="default"
                            onClick={() => handleUpdateStatus(transfer.id, "ACCEPTED")}
                          >
                            Accept
                          </Button>
                          <Button
                            size="sm"
                            variant="destructive"
                            onClick={() => handleUpdateStatus(transfer.id, "REJECTED")}
                          >
                            Reject
                          </Button>
                        </div>
                      )}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        )}
      </CardContent>
    </Card>
  )
}
