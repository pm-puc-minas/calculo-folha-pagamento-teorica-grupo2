"use client"

import { Line, LineChart, ResponsiveContainer, Tooltip, XAxis, YAxis } from "recharts"

const data = [
  { month: "Jan", value: 425000 },
  { month: "Fev", value: 438000 },
  { month: "Mar", value: 445000 },
  { month: "Abr", value: 462000 },
  { month: "Mai", value: 471000 },
  { month: "Jun", value: 487500 },
]

export function PayrollChart() {
  return (
    <ResponsiveContainer width="100%" height={300}>
      <LineChart data={data}>
        <XAxis dataKey="month" stroke="oklch(0.55 0 0)" fontSize={12} tickLine={false} axisLine={false} />
        <YAxis
          stroke="oklch(0.55 0 0)"
          fontSize={12}
          tickLine={false}
          axisLine={false}
          tickFormatter={(value) => `R$ ${(value / 1000).toFixed(0)}k`}
        />
        <Tooltip
          content={({ active, payload }) => {
            if (active && payload && payload.length) {
              return (
                <div className="rounded-lg border border-border bg-card p-3 shadow-lg">
                  <div className="text-sm font-medium text-foreground">
                    R$ {payload[0].value?.toLocaleString("pt-BR")}
                  </div>
                  <div className="text-xs text-muted-foreground">{payload[0].payload.month}</div>
                </div>
              )
            }
            return null
          }}
        />
        <Line type="monotone" dataKey="value" stroke="oklch(0.65 0.25 250)" strokeWidth={2} dot={false} />
      </LineChart>
    </ResponsiveContainer>
  )
}
