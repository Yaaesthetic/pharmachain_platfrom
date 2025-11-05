"use client"

import { useState, useEffect } from "react"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Input } from "@/components/ui/input"
import { Textarea } from "@/components/ui/textarea"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { mockApi } from "@/lib/api-service"
import type { DeliveryItem } from "@/types"
import { getStatusColor, formatDate } from "@/lib/utils-pharma"

export const DeliveryItemManagement = () => {
  const [deliveryItems, setDeliveryItems] = useState<DeliveryItem[]>([])
  const [selectedItem, setSelectedItem] = useState<DeliveryItem | null>(null)
  const [isProofOpen, setIsProofOpen] = useState(false)
  const [proofData, setProofData] = useState({ deliveryNotes: "", recipientSignature: "" })
  const [filterStatus, setFilterStatus] = useState("all")

  useEffect(() => {
    loadDeliveryItems()
  }, [])

  const loadDeliveryItems = async () => {
    const data = await mockApi.getDeliveryItems()
    setDeliveryItems(data.content)
  }

  const handleUpdateProof = async () => {
    if (selectedItem) {
      await mockApi.updateProof(selectedItem.blNumber, proofData)
      setIsProofOpen(false)
      setProofData({ deliveryNotes: "", recipientSignature: "" })
      loadDeliveryItems()
    }
  }

  const filteredItems = deliveryItems.filter((item) => filterStatus === "all" || item.status === filterStatus)

  return (
    <Card>
      <CardHeader>
        <CardTitle>Delivery Item Management</CardTitle>
      </CardHeader>
      <CardContent>
        <div className="mb-4">
          <Select value={filterStatus} onValueChange={setFilterStatus}>
            <SelectTrigger>
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Status</SelectItem>
              <SelectItem value="PENDING">Pending</SelectItem>
              <SelectItem value="IN_TRANSIT">In Transit</SelectItem>
              <SelectItem value="DELIVERED">Delivered</SelectItem>
              <SelectItem value="FAILED">Failed</SelectItem>
            </SelectContent>
          </Select>
        </div>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>BL Number</TableHead>
              <TableHead>Bordereau</TableHead>
              <TableHead>Client</TableHead>
              <TableHead>Colis</TableHead>
              <TableHead>Sachets</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Delivered At</TableHead>
              <TableHead>Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {filteredItems.map((item) => (
              <TableRow key={item.blNumber}>
                <TableCell>{item.blNumber}</TableCell>
                <TableCell>{item.bordereau?.bordereauNumber || "N/A"}</TableCell>
                <TableCell>{item.client?.name || "N/A"}</TableCell>
                <TableCell>{item.nombreColis}</TableCell>
                <TableCell>{item.nombreSachets}</TableCell>
                <TableCell>
                  <Badge className={getStatusColor(item.status)}>{item.status}</Badge>
                </TableCell>
                <TableCell>{formatDate(item.deliveredAt)}</TableCell>
                <TableCell>
                  {item.status !== "DELIVERED" && (
                    <Button
                      size="sm"
                      onClick={() => {
                        setSelectedItem(item)
                        setIsProofOpen(true)
                      }}
                    >
                      Add Proof
                    </Button>
                  )}
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>

        <Dialog open={isProofOpen} onOpenChange={setIsProofOpen}>
          <DialogContent>
            <DialogHeader>
              <DialogTitle>Add Delivery Proof - {selectedItem?.blNumber}</DialogTitle>
            </DialogHeader>
            <div className="space-y-4">
              <div>
                <Label>Delivery Notes</Label>
                <Textarea
                  value={proofData.deliveryNotes}
                  onChange={(e) => setProofData({ ...proofData, deliveryNotes: e.target.value })}
                  placeholder="Enter delivery notes..."
                />
              </div>
              <div>
                <Label>Recipient Signature</Label>
                <Input
                  value={proofData.recipientSignature}
                  onChange={(e) => setProofData({ ...proofData, recipientSignature: e.target.value })}
                  placeholder="Signed by..."
                />
              </div>
              <Button onClick={handleUpdateProof} className="w-full">
                Submit Proof
              </Button>
            </div>
          </DialogContent>
        </Dialog>
      </CardContent>
    </Card>
  )
}
