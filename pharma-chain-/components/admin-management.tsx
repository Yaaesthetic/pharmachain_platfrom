"use client"

import { useState, useEffect } from "react"
import { useAuth } from "@/lib/auth-context"
import { Card, CardHeader, CardTitle, CardContent } from "@/components/ui/card"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Badge } from "@/components/ui/badge"
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from "@/components/ui/dialog"
import { Label } from "@/components/ui/label"
import { Alert, AlertDescription } from "@/components/ui/alert"
import { Table, TableHeader, TableBody, TableRow, TableHead, TableCell } from "./table-components"
import { apiClient } from "@/lib/api-client"
import type { Admin } from "@/types"
import { formatDate } from "@/lib/utils-pharma"
import { Loader2, AlertCircle } from "lucide-react"

export const AdminManagement = () => {
  const { user } = useAuth()
  const [admins, setAdmins] = useState<Admin[]>([])
  const [isCreateOpen, setIsCreateOpen] = useState(false)
  const [isLoading, setIsLoading] = useState(false)
  const [error, setError] = useState<string | null>(null)
  const [formData, setFormData] = useState({
    code: "",
    username: "",
    email: "",
    firstName: "",
    lastName: "",
    password: "",
  })
  const [filterUsername, setFilterUsername] = useState("")

  useEffect(() => {
    loadAdmins()
  }, [])

  const loadAdmins = async () => {
    try {
      setError(null)
      setIsLoading(true)
      const data = await apiClient.getAdmins()
      setAdmins(data.content)
    } catch (err: any) {
      console.error("Failed to load admins:", err)
      setError(err.message || "Failed to load admins")
    } finally {
      setIsLoading(false)
    }
  }

  const handleCreate = async () => {
    try {
      setError(null)
      await apiClient.createAdmin(formData)
      setIsCreateOpen(false)
      setFormData({
        code: "",
        username: "",
        email: "",
        firstName: "",
        lastName: "",
        password: "",
      })
      await loadAdmins()
      alert("Admin created successfully!")
    } catch (err: any) {
      console.error("Failed to create admin:", err)
      setError(err.message || "Failed to create admin")
    }
  }

  const handleDelete = async (id: number) => {
    if (user?.id === id) {
      alert("You cannot delete your own account")
      return
    }

    if (window.confirm("Are you sure you want to delete this admin?")) {
      try {
        setError(null)
        await apiClient.deleteAdmin(id)
        await loadAdmins()
        alert("Admin deleted successfully!")
      } catch (err: any) {
        console.error("Failed to delete admin:", err)
        setError(err.message || "Failed to delete admin")
      }
    }
  }

  const filteredAdmins = admins.filter(
    (admin) => !filterUsername || admin.username.toLowerCase().includes(filterUsername.toLowerCase()),
  )

  return (
    <Card>
      <CardHeader>
        <CardTitle className="flex justify-between items-center">
          <div>
            <span>Admin Management</span>
            {user && (
              <p className="text-sm text-gray-500 mt-1">
                Logged in as: {user.firstName} {user.lastName} ({user.code})
              </p>
            )}
          </div>
          <Dialog open={isCreateOpen} onOpenChange={setIsCreateOpen}>
            <DialogTrigger asChild>
              <Button>Create Admin</Button>
            </DialogTrigger>
            <DialogContent>
              <DialogHeader>
                <DialogTitle>Create New Admin</DialogTitle>
              </DialogHeader>
              <div className="space-y-4">
                {error && (
                  <Alert variant="destructive">
                    <AlertCircle className="h-4 w-4" />
                    <AlertDescription>{error}</AlertDescription>
                  </Alert>
                )}
                <div>
                  <Label>Code</Label>
                  <Input
                    value={formData.code}
                    onChange={(e) => setFormData({ ...formData, code: e.target.value })}
                    placeholder="300001"
                  />
                </div>
                <div>
                  <Label>Username</Label>
                  <Input
                    value={formData.username}
                    onChange={(e) => setFormData({ ...formData, username: e.target.value })}
                    placeholder="admin1"
                  />
                </div>
                <div>
                  <Label>Email</Label>
                  <Input
                    type="email"
                    value={formData.email}
                    onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                    placeholder="admin@pharmachain.ma"
                  />
                </div>
                <div>
                  <Label>First Name</Label>
                  <Input
                    value={formData.firstName}
                    onChange={(e) => setFormData({ ...formData, firstName: e.target.value })}
                    placeholder="John"
                  />
                </div>
                <div>
                  <Label>Last Name</Label>
                  <Input
                    value={formData.lastName}
                    onChange={(e) => setFormData({ ...formData, lastName: e.target.value })}
                    placeholder="Doe"
                  />
                </div>
                <div>
                  <Label>Password</Label>
                  <Input
                    type="password"
                    value={formData.password}
                    onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                    placeholder="SecurePassword123!"
                  />
                </div>
                <Button onClick={handleCreate} className="w-full" disabled={isLoading}>
                  {isLoading ? (
                    <>
                      <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                      Creating...
                    </>
                  ) : (
                    "Create"
                  )}
                </Button>
              </div>
            </DialogContent>
          </Dialog>
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
            placeholder="Filter by username..."
            value={filterUsername}
            onChange={(e) => setFilterUsername(e.target.value)}
          />
        </div>
        {isLoading && filteredAdmins.length === 0 ? (
          <div className="flex items-center justify-center py-8">
            <Loader2 className="h-6 w-6 animate-spin text-teal-600" />
            <span className="ml-2 text-slate-600">Loading admins...</span>
          </div>
        ) : (
          <Table>
            <TableHeader>
              <TableRow>
                <TableHead>ID</TableHead>
                <TableHead>Code</TableHead>
                <TableHead>Username</TableHead>
                <TableHead>Email</TableHead>
                <TableHead>Status</TableHead>
                <TableHead>Created At</TableHead>
                <TableHead>Actions</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {filteredAdmins.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={7} className="text-center py-4 text-gray-500">
                    No admins found
                  </TableCell>
                </TableRow>
              ) : (
                filteredAdmins.map((admin) => (
                  <TableRow key={admin.id}>
                    <TableCell>{admin.id}</TableCell>
                    <TableCell>{admin.code}</TableCell>
                    <TableCell>
                      {admin.username}
                      {user?.id === admin.id && <span className="ml-2 text-xs text-blue-600 font-semibold">(You)</span>}
                    </TableCell>
                    <TableCell>{admin.email ?? "********"}</TableCell>
                    <TableCell>
                      <Badge className={admin.isActive ? "bg-green-500" : "bg-red-500"}>
                        {admin.isActive ? "Active" : "Inactive"}
                      </Badge>
                    </TableCell>
                    <TableCell>{formatDate(admin.createdAt)}</TableCell>
                    <TableCell>
                      <Button
                        variant="destructive"
                        size="sm"
                        onClick={() => handleDelete(admin.id)}
                        disabled={user?.id === admin.id || isLoading}
                      >
                        Delete
                      </Button>
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
