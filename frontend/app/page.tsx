import { PayrollCalculator } from "@/components/payroll-calculator"

export default function Home() {
  return (
    <main className="min-h-screen bg-background">
      <div className="border-b border-border bg-card">
        <div className="container mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-foreground">Sistema de Folha de Pagamento</h1>
              <p className="text-muted-foreground mt-1">Calculadora integrada com backend Spring Boot</p>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <PayrollCalculator />
      </div>
    </main>
  )
}
