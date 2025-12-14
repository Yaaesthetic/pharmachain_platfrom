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
    <nav className="sticky top-0 z-20 border-b border-slate-200/70 bg-white/85 backdrop-blur supports-[backdrop-filter]:bg-white/70 dark:border-slate-800 dark:bg-slate-900/85">
      <div className="relative mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">
        <div className="pointer-events-none absolute inset-0 opacity-70">
          <div className="absolute inset-0 bg-gradient-to-r from-teal-50/60 via-transparent to-transparent dark:from-teal-900/10" />
        </div>
        <div className="relative flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-teal-600 text-sm font-semibold text-white shadow-sm ring-1 ring-teal-500/20">
            PC
          </div>
          <div>
            <p className="text-[11px] uppercase tracking-[0.28em] text-slate-500">PharmaChain</p>
            <h1 className="text-lg font-semibold text-slate-900 dark:text-white">Control Dashboard</h1>
          </div>
        </div>
        <div className="relative flex items-center gap-4">
          <div className="text-right text-sm text-slate-600 dark:text-slate-300">
            <p className="font-semibold text-slate-900 dark:text-white">
              {user.firstName} {user.lastName}
            </p>
            <p className="text-xs text-slate-500 dark:text-slate-400">{user.roles?.join(", ") || "No Role"}</p>
          </div>
          <Avatar>
            <AvatarFallback className="bg-teal-100 text-teal-800 font-semibold dark:bg-teal-900/40 dark:text-teal-100">
              {userInitials}
            </AvatarFallback>
          </Avatar>
          <Button
            onClick={logout}
            variant="outline"
            size="sm"
            className="border-slate-200 text-slate-700 shadow-sm transition hover:-translate-y-0.5 hover:border-teal-200 hover:text-teal-900 dark:border-slate-800 dark:text-slate-200 dark:hover:border-teal-800"
          >
            <LogOut className="mr-2 h-4 w-4" />
            Logout
          </Button>
        </div>
      </div>
    </nav>
  )
}
