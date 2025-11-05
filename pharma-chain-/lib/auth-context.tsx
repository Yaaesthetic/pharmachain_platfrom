"use client"

import type React from "react"
import { createContext, useContext, useState, useEffect } from "react"
import { useRouter } from "next/navigation"
import { authenticateWithKeycloak } from "@/app/actions/auth-actions"

interface User {
  keycloakUserId: string
  username: string
  email: string
  firstName: string
  lastName: string
  code: string
  roles: string[]
}

interface AuthContextType {
  user: User | null
  accessToken: string | null
  isAuthenticated: boolean
  isLoading: boolean
  login: (username: string, password: string) => Promise<void>
  logout: () => void
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<User | null>(null)
  const [accessToken, setAccessToken] = useState<string | null>(null)
  const [isLoading, setIsLoading] = useState(true)
  const router = useRouter()

  // Load user from localStorage on mount
  useEffect(() => {
    const storedToken = localStorage.getItem("accessToken")
    const storedUser = localStorage.getItem("user")

    if (storedToken && storedUser) {
      setAccessToken(storedToken)
      setUser(JSON.parse(storedUser))
    }
    setIsLoading(false)
  }, [])

  const login = async (username: string, password: string) => {
    try {
      const tokens = await authenticateWithKeycloak(username, password)
      const { access_token, refresh_token } = tokens

      // Step 2: Decode JWT to extract user info
      const decodedToken = decodeJWT(access_token)

      const userData: User = {
        keycloakUserId: decodedToken.sub,
        username: decodedToken.preferred_username,
        email: decodedToken.email,
        firstName: decodedToken.given_name,
        lastName: decodedToken.family_name,
        code: decodedToken.code,
        roles: decodedToken.realm_access?.roles || [],
      }

      // Step 3: Store in state and localStorage
      setAccessToken(access_token)
      setUser(userData)
      localStorage.setItem("accessToken", access_token)
      localStorage.setItem("refreshToken", refresh_token)
      localStorage.setItem("user", JSON.stringify(userData))

      // Step 4: Redirect to dashboard
      router.push("/")
    } catch (error: any) {
      console.error("[v0] Login failed:", error)
      throw error
    }
  }

  const logout = () => {
    setAccessToken(null)
    setUser(null)
    localStorage.removeItem("accessToken")
    localStorage.removeItem("refreshToken")
    localStorage.removeItem("user")
    router.push("/login")
  }

  return (
    <AuthContext.Provider
      value={{
        user,
        accessToken,
        isAuthenticated: !!user,
        isLoading,
        login,
        logout,
      }}
    >
      {children}
    </AuthContext.Provider>
  )
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider")
  }
  return context
}

// Helper function to decode JWT
function decodeJWT(token: string): any {
  try {
    const base64Url = token.split(".")[1]
    const base64 = base64Url.replace(/-/g, "+").replace(/_/g, "/")
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split("")
        .map((c) => "%" + ("00" + c.charCodeAt(0).toString(16)).slice(-2))
        .join(""),
    )
    return JSON.parse(jsonPayload)
  } catch (error) {
    console.error("[v0] Failed to decode JWT:", error)
    return {}
  }
}
