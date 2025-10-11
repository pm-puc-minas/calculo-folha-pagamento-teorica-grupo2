"use client"

import { useState } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Users, DollarSign, TrendingUp, Calculator, Download, Plus } from "lucide-react"
import { EmployeeTable } from "@/components/employee-table"
import { PayrollCalculator } from "@/components/payroll-calculator"
import { PayrollChart } from "@/components/payroll-chart"

export function PayrollDashboard() {
  const [activeTab, setActiveTab] = useState<"overview" | "calculator">("overview")

  const stats = [
    {
      title: "Total de Funcionários",
      value: "248",
      change: "+12%",
      icon: Users,
      trend: "up",
    },
    {
      title: "Folha Total",
      value: "R$ 487.500",
      change: "+8.2%",
      icon: DollarSign,
      trend: "up",
    },
    {
      title: "Média Salarial",
      value: "R$ 5.850",
      change: "+3.1%",
      icon: TrendingUp,
      trend: "up",
    },
    {
      title: "Descontos Totais",
      value: "R$ 98.200",
      change: "-2.4%",
      icon: Calculator,
      trend: "down",
    },
  ]

  return (
    <div className="min-h-screen bg-background">
      <div className="border-b border-border bg-card">
        <div className="container mx-auto px-4 py-6">
          <div className="flex items-center justify-between">
            <div>
              <h1 className="text-3xl font-bold text-foreground">Folha de Pagamento</h1>
              <p className="text-muted-foreground mt-1">Gerencie e calcule a folha de pagamento da sua empresa</p>
            </div>
            <div className="flex gap-3">
              <Button variant="outline" size="sm">
                <Download className="mr-2 h-4 w-4" />
                Exportar
              </Button>
              <Button size="sm" className="bg-primary hover:bg-primary/90">
                <Plus className="mr-2 h-4 w-4" />
                Novo Funcionário
              </Button>
            </div>
          </div>
        </div>
      </div>

      <div className="container mx-auto px-4 py-8">
        <div className="flex gap-2 mb-6">
          <Button variant={activeTab === "overview" ? "default" : "outline"} onClick={() => setActiveTab("overview")}>
            Visão Geral
          </Button>
          <Button
            variant={activeTab === "calculator" ? "default" : "outline"}
            onClick={() => setActiveTab("calculator")}
          >
            Calculadora
          </Button>
        </div>

        {activeTab === "overview" ? (
          <>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-4 mb-8">
              {stats.map((stat) => (
                <Card key={stat.title} className="bg-card border-border">
                  <CardHeader className="flex flex-row items-center justify-between pb-2">
                    <CardTitle className="text-sm font-medium text-muted-foreground">{stat.title}</CardTitle>
                    <stat.icon className="h-4 w-4 text-muted-foreground" />
                  </CardHeader>
                  <CardContent>
                    <div className="text-2xl font-bold text-foreground">{stat.value}</div>
                    <Badge
                      variant="secondary"
                      className={`mt-2 ${
                        stat.trend === "up" ? "bg-accent/20 text-accent" : "bg-destructive/20 text-destructive"
                      }`}
                    >
                      {stat.change} vs mês anterior
                    </Badge>
                  </CardContent>
                </Card>
              ))}
            </div>

            <div className="grid gap-6 lg:grid-cols-3 mb-8">
              <Card className="lg:col-span-2 bg-card border-border">
                <CardHeader>
                  <CardTitle className="text-foreground">Evolução da Folha</CardTitle>
                  <CardDescription className="text-muted-foreground">Últimos 6 meses</CardDescription>
                </CardHeader>
                <CardContent>
                  <PayrollChart />
                </CardContent>
              </Card>

              <Card className="bg-card border-border">
                <CardHeader>
                  <CardTitle className="text-foreground">Distribuição por Departamento</CardTitle>
                  <CardDescription className="text-muted-foreground">Folha atual</CardDescription>
                </CardHeader>
                <CardContent className="space-y-4">
                  {[
                    { dept: "Tecnologia", value: "R$ 185.000", percent: 38 },
                    { dept: "Vendas", value: "R$ 142.500", percent: 29 },
                    { dept: "Operações", value: "R$ 97.500", percent: 20 },
                    { dept: "Administrativo", value: "R$ 62.500", percent: 13 },
                  ].map((item) => (
                    <div key={item.dept} className="space-y-2">
                      <div className="flex items-center justify-between text-sm">
                        <span className="text-foreground font-medium">{item.dept}</span>
                        <span className="text-muted-foreground">{item.value}</span>
                      </div>
                      <div className="h-2 bg-secondary rounded-full overflow-hidden">
                        <div className="h-full bg-primary rounded-full" style={{ width: `${item.percent}%` }} />
                      </div>
                    </div>
                  ))}
                </CardContent>
              </Card>
            </div>

            <EmployeeTable />
          </>
        ) : (
          <PayrollCalculator />
        )}
      </div>
    </div>
  )
}
