"use client"

import type React from "react"

import { useAuth } from "@/lib/auth-context"
import { useRouter } from "next/navigation"
import { useEffect } from "react"
import { AlertCircle } from "lucide-react"

interface RoleGuardProps {
  children: React.ReactNode
  allowedRoles: string[]
  fallbackUrl?: string
}

export function RoleGuard({ children, allowedRoles, fallbackUrl = "/" }: RoleGuardProps) {
  const { user, isLoading } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (isLoading) return

    if (!user) {
      router.push("/login")
      return
    }

    const userRoles = (user.roles || []).map((role) => role.toLowerCase())
    const normalizedAllowedRoles = allowedRoles.map((role) => role.toLowerCase())
    const hasPermission = normalizedAllowedRoles.some((role) => userRoles.includes(role))

    console.log(
      "[v0] Role check - User roles:",
      userRoles,
      "Allowed:",
      normalizedAllowedRoles,
      "Has permission:",
      hasPermission,
    )

    if (!hasPermission) {
      router.push(fallbackUrl)
    }
  }, [user, isLoading, allowedRoles, fallbackUrl, router])

  if (isLoading) {
    return <div>Loading...</div>
  }

  if (!user) {
    return null
  }

  const userRoles = (user.roles || []).map((role) => role.toLowerCase())
  const normalizedAllowedRoles = allowedRoles.map((role) => role.toLowerCase())
  const hasPermission = normalizedAllowedRoles.some((role) => userRoles.includes(role))

  if (!hasPermission) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="text-center">
          <AlertCircle className="h-12 w-12 text-red-600 mx-auto mb-4" />
          <h1 className="text-2xl font-bold mb-2">Access Denied</h1>
          <p className="text-gray-600">You don't have permission to access this resource.</p>
          <p className="text-sm text-gray-500 mt-4">Your roles: {userRoles.join(", ") || "None"}</p>
        </div>
      </div>
    )
  }

  return <>{children}</>
}
