"use client"

import { useState, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { mockApi } from "@/lib/api-service"
import type { Bordereau, DeliveryItem } from "@/types"
import { getStatusColor, formatDate } from "@/lib/utils-pharma"

export const BordereauManagement = () => {
  const [bordereaux, setBordereaux] = useState<Bordereau[]>([])
  const [selectedBordereau, setSelectedBordereau] = useState<Bordereau | null>(null)
  const [deliveryItems, setDeliveryItems] = useState<DeliveryItem[]>([])
  const [filterStatus, setFilterStatus] = useState("")

  useEffect(() => {
    loadBordereaux()
  }, [])

  const loadBordereaux = async () => {
    const data = await mockApi.getBordereaux()
    setBordereaux(data.content)
  }

  const loadBordereauDetails = async (bordereauNumber: string) => {
    const items = await mockApi.getBordereauDeliveryItems(bordereauNumber)
    setDeliveryItems(items)
  }

  const filteredBordereaux = bordereaux.filter(
    (bordereau) => !filterStatus || filterStatus === "ALL" || bordereau.status === filterStatus,
  )

  return (
    <Card>
      <CardHeader>
        <CardTitle>Bordereau Management</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="mb-4">
          <Select value={filterStatus} onValueChange={setFilterStatus}>
            <SelectTrigger>
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="ALL">All Status</SelectItem>
              <SelectItem value="PENDING">Pending</SelectItem>
              <SelectItem value="IN_PROGRESS">In Progress</SelectItem>
              <SelectItem value="COMPLETED">Completed</SelectItem>
              <SelectItem value="CANCELLED">Cancelled</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Number</TableHead>
              <TableHead>Delivery Date</TableHead>
              <TableHead>Driver</TableHead>
              <TableHead>Secteur</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Scanned At</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredBordereaux.map((bordereau) => (
              <TableRow key={bordereau.bordereauNumber}>
                <TableCell>{bordereau.bordereauNumber}</TableCell>
                <TableCell>{bordereau.deliveryDate}</TableCell>
                <TableCell>{bordereau.currentDriver?.username || "N/A"}</TableCell>
                <TableCell>{bordereau.secteur?.secteurName || "N/A"}</TableCell>
                <TableCell>
                  <Badge className={getStatusColor(bordereau.status)}>{bordereau.status}</Badge>
                </TableCell>
                <TableCell>{formatDate(bordereau.scannedAt)}</TableCell>
                <TableCell>
                  <Button
                    size="sm"
                    onClick={() => {
                      setSelectedBordereau(bordereau)
                      loadBordereauDetails(bordereau.bordereauNumber)
                    }}
                  >
                    View Items
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        {selectedBordereau && (
          <div className="mt-6">
            <h3 className="text-lg font-bold mb-4">
              Bordereau: {selectedBordereau.bordereauNumber} - Delivery Items ({deliveryItems.length})
            </h3>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>BL Number</TableHead>
                  <TableHead>Client</TableHead>
                  <TableHead>Colis</TableHead>
                  <TableHead>Sachets</TableHead>
                  <TableHead>Status</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {deliveryItems.map((item) => (
                  <TableRow key={item.blNumber}>
                    <TableCell>{item.blNumber}</TableCell>
                    <TableCell>{item.client?.name || "N/A"}</TableCell>
                    <TableCell>{item.nombreColis}</TableCell>
                    <TableCell>{item.nombreSachets}</TableCell>
                    <TableCell>
                      <Badge className={getStatusColor(item.status)}>{item.status}</Badge>
                    </TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </div>
        )}
      </CardContent>
    </Card>
  )
}
