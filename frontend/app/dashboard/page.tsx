"use client"

import { useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { PayrollCalculator } from '@/components/payroll-calculator'
import { Loader2 } from 'lucide-react'

export default function DashboardPage() {
  const { isAuthenticated, loading } = useAuth()
  const router = useRouter()

  useEffect(() => {
    if (!loading && !isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, loading, router])

  if (loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <div className="text-center">
          <Loader2 className="h-12 w-12 animate-spin mx-auto text-primary mb-4" />
          <p className="text-muted-foreground">Carregando...</p>
        </div>
      </div>
    )
  }

  if (!isAuthenticated) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      <main className="container mx-auto px-4 py-6 max-w-7xl">
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-foreground mb-1.5">
            Cálculo de Folha
          </h2>
          <p className="text-sm text-muted-foreground">
            Gestão de folha de pagamento para funcionários CLT e PJ
          </p>
        </div>

        <PayrollCalculator />
      </main>
    </div>
  )
}

