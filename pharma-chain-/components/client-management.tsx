"use client"

import { useState, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { mockApi } from "@/lib/api-service"
import type { Client, DeliveryItem } from "@/types"
import { getStatusColor, formatDate } from "@/lib/utils-pharma"

export const ClientManagement = () => {
  const [clients, setClients] = useState<Client[]>([])
  const [selectedClient, setSelectedClient] = useState<Client | null>(null)
  const [clientDeliveryItems, setClientDeliveryItems] = useState<DeliveryItem[]>([])
  const [filterName, setFilterName] = useState("")

  useEffect(() => {
    loadClients()
  }, [])

  const loadClients = async () => {
    const data = await mockApi.getClients()
    setClients(data.content)
  }

  const loadClientDetails = async (clientCode: string) => {
    const items = await mockApi.getClientDeliveryItems(clientCode)
    setClientDeliveryItems(items)
  }

  const filteredClients = clients.filter(
    (client) => !filterName || client.name.toLowerCase().includes(filterName.toLowerCase()),
  )

  return (
    <Card>
      <CardHeader>
        <CardTitle>Client Management</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="mb-4">
          <Input placeholder="Filter by name..." value={filterName} onChange={(e) => setFilterName(e.target.value)} />
        </div>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Code</TableHead>
              <TableHead>Name</TableHead>
              <TableHead>Address</TableHead>
              <TableHead>Phone</TableHead>
              <TableHead>Secteur</TableHead>
              <TableHead>Auto-Created</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredClients.map((client) => (
              <TableRow key={client.clientCode}>
                <TableCell>{client.clientCode}</TableCell>
                <TableCell>{client.name}</TableCell>
                <TableCell>{client.address}</TableCell>
                <TableCell>{client.phone}</TableCell>
                <TableCell>{client.secteur?.secteurName || "N/A"}</TableCell>
                <TableCell>
                  <Badge className={client.autoCreated ? "bg-blue-500" : "bg-gray-500"}>
                    {client.autoCreated ? "Yes" : "No"}
                  </Badge>
                </TableCell>
                <TableCell>
                  <Button
                    size="sm"
                    onClick={() => {
                      setSelectedClient(client)
                      loadClientDetails(client.clientCode)
                    }}
                  >
                    View Items
                  </Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        {selectedClient && (
          <div className="mt-6">
            <h3 className="text-lg font-bold mb-4">
              Client: {selectedClient.name} - Delivery Items ({clientDeliveryItems.length})
            </h3>
            <div className="grid grid-cols-4 gap-4 mb-4">
              <Card>
                <CardContent className="pt-6">
                  <div className="text-2xl font-bold">
                    {clientDeliveryItems.filter((i) => i.status === "DELIVERED").length}
                  </div>
                  <div className="text-sm text-gray-500">Delivered</div>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="pt-6">
                  <div className="text-2xl font-bold">
                    {clientDeliveryItems.filter((i) => i.status === "PENDING").length}
                  </div>
                  <div className="text-sm text-gray-500">Pending</div>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="pt-6">
                  <div className="text-2xl font-bold">
                    {clientDeliveryItems.reduce((sum, i) => sum + (i.nombreColis || 0), 0)}
                  </div>
                  <div className="text-sm text-gray-500">Total Colis</div>
                </CardContent>
              </Card>
              <Card>
                <CardContent className="pt-6">
                  <div className="text-2xl font-bold">
                    {clientDeliveryItems.reduce((sum, i) => sum + (i.nombreSachets || 0), 0)}
                  </div>
                  <div className="text-sm text-gray-500">Total Sachets</div>
                </CardContent>
              </Card>
            </div>
            <Table>
              <TableHeader>
                <TableRow>
                  <TableHead>BL Number</TableHead>
                  <TableHead>Colis</TableHead>
                  <TableHead>Sachets</TableHead>
                  <TableHead>Status</TableHead>
                  <TableHead>Delivered At</TableHead>
                </TableRow>
              </TableHeader>
              <TableBody>
                {clientDeliveryItems.map((item) => (
                  <TableRow key={item.blNumber}>
                    <TableCell>{item.blNumber}</TableCell>
                    <TableCell>{item.nombreColis}</TableCell>
                    <TableCell>{item.nombreSachets}</TableCell>
                    <TableCell>
                      <Badge className={getStatusColor(item.status)}>{item.status}</Badge>
                    </TableCell>
                    <TableCell>{formatDate(item.deliveredAt)}</TableCell>
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
