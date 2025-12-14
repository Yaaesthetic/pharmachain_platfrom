import { PharmaChainApp } from "@/components/pharma-chain-app"
import { Navbar } from "@/components/navbar"
import { ProtectedRoute } from "@/components/auth/protected-route"

export default function Home() {
  return (
    <ProtectedRoute>
      <div className="flex flex-col min-h-screen">
        <Navbar />
        <main className="flex-1 bg-gradient-to-b from-white/70 via-white to-slate-50 dark:from-slate-900 dark:via-slate-950 dark:to-slate-950">
          <div className="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8 md:py-12">
            <div className="pointer-events-none absolute inset-0 rounded-3xl bg-[radial-gradient(circle_at_20%_20%,rgba(14,165,153,0.10)_0,transparent_30%),radial-gradient(circle_at_80%_0,rgba(14,165,153,0.08)_0,transparent_26%)]" />
            <div className="relative">
              <PharmaChainApp />
            </div>
          </div>
        </main>
      </div>
    </ProtectedRoute>
  )
}
