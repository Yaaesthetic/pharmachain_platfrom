import { PharmaChainApp } from "@/components/pharma-chain-app"
import { Navbar } from "@/components/navbar"
import { ProtectedRoute } from "@/components/auth/protected-route"

export default function Home() {
  return (
    <ProtectedRoute>
      <div className="flex flex-col min-h-screen">
        <Navbar />
        <main className="flex-1 bg-gray-50">
          <PharmaChainApp />
        </main>
      </div>
    </ProtectedRoute>
  )
}
