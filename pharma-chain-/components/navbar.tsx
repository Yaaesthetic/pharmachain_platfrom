"use client"

import { useAuth } from "@/lib/auth-context"
import { Button } from "@/components/ui/button"
import { Avatar, AvatarFallback } from "@/components/ui/avatar"
import { LogOut } from "lucide-react"

export function Navbar() {
  const { user, logout } = useAuth()

  if (!user) return null

  const userInitials = `${user.firstName?.[0] || ""}${user.lastName?.[0] || ""}`.toUpperCase()

  return (
    <nav className="bg-white shadow-sm border-b border-gray-200">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div className="flex justify-between h-16 items-center">
          <div className="flex items-center">
            <h1 className="text-xl font-bold text-gray-900">PharmaChain</h1>
          </div>
          <div className="flex items-center gap-4">
            <div className="text-sm text-gray-600">
              <p className="font-medium">
                {user.firstName} {user.lastName}
              </p>
              <p className="text-xs text-gray-500">{user.roles?.join(", ") || "No Role"}</p>
            </div>
            <Avatar>
              <AvatarFallback className="bg-blue-100 text-blue-700 font-semibold">{userInitials}</AvatarFallback>
            </Avatar>
            <Button onClick={logout} variant="outline" size="sm">
              <LogOut className="mr-2 h-4 w-4" />
              Logout
            </Button>
          </div>
        </div>
      </div>
    </nav>
  )
}
