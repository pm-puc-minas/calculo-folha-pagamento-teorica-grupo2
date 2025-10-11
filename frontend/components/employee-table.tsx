"use client"

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card"
import { Badge } from "@/components/ui/badge"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Search, MoreVertical } from "lucide-react"

const employees = [
  {
    id: 1,
    name: "Ana Silva",
    role: "Desenvolvedora Senior",
    department: "Tecnologia",
    salary: 12000,
    inss: 828.38,
    irrf: 2020.59,
    status: "Ativo",
  },
  {
    id: 2,
    name: "Carlos Santos",
    role: "Gerente de Vendas",
    department: "Vendas",
    salary: 9500,
    inss: 828.38,
    irrf: 1280.53,
    status: "Ativo",
  },
  {
    id: 3,
    name: "Maria Oliveira",
    role: "Analista de RH",
    department: "Administrativo",
    salary: 5500,
    inss: 605.0,
    irrf: 413.43,
    status: "Ativo",
  },
  {
    id: 4,
    name: "João Pereira",
    role: "Coordenador de Operações",
    department: "Operações",
    salary: 7800,
    inss: 828.38,
    irrf: 863.53,
    status: "Ativo",
  },
  {
    id: 5,
    name: "Fernanda Costa",
    role: "Designer UX/UI",
    department: "Tecnologia",
    salary: 8500,
    inss: 828.38,
    irrf: 1025.03,
    status: "Férias",
  },
]

export function EmployeeTable() {
  return (
    <Card className="bg-card border-border">
      <CardHeader>
        <div className="flex items-center justify-between">
          <CardTitle className="text-foreground">Funcionários</CardTitle>
          <div className="relative w-64">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
            <Input placeholder="Buscar funcionário..." className="pl-9 bg-secondary border-border text-foreground" />
          </div>
        </div>
      </CardHeader>
      <CardContent>
        <div className="overflow-x-auto">
          <table className="w-full">
            <thead>
              <tr className="border-b border-border">
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Nome</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Cargo</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Departamento</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Salário Bruto</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">INSS</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">IRRF</th>
                <th className="text-right py-3 px-4 text-sm font-medium text-muted-foreground">Líquido</th>
                <th className="text-left py-3 px-4 text-sm font-medium text-muted-foreground">Status</th>
                <th className="w-12"></th>
              </tr>
            </thead>
            <tbody>
              {employees.map((employee) => {
                const liquid = employee.salary - employee.inss - employee.irrf
                return (
                  <tr key={employee.id} className="border-b border-border hover:bg-secondary/50">
                    <td className="py-4 px-4">
                      <div className="font-medium text-foreground">{employee.name}</div>
                    </td>
                    <td className="py-4 px-4 text-sm text-muted-foreground">{employee.role}</td>
                    <td className="py-4 px-4 text-sm text-muted-foreground">{employee.department}</td>
                    <td className="py-4 px-4 text-right font-mono text-foreground">
                      R$ {employee.salary.toLocaleString("pt-BR")}
                    </td>
                    <td className="py-4 px-4 text-right font-mono text-destructive">-R$ {employee.inss.toFixed(2)}</td>
                    <td className="py-4 px-4 text-right font-mono text-destructive">-R$ {employee.irrf.toFixed(2)}</td>
                    <td className="py-4 px-4 text-right font-mono font-semibold text-accent">R$ {liquid.toFixed(2)}</td>
                    <td className="py-4 px-4">
                      <Badge
                        variant="secondary"
                        className={
                          employee.status === "Ativo" ? "bg-accent/20 text-accent" : "bg-chart-3/20 text-chart-3"
                        }
                      >
                        {employee.status}
                      </Badge>
                    </td>
                    <td className="py-4 px-4">
                      <Button variant="ghost" size="sm">
                        <MoreVertical className="h-4 w-4" />
                      </Button>
                    </td>
                  </tr>
                )
              })}
            </tbody>
          </table>
        </div>
      </CardContent>
    </Card>
  )
}
