"use client"

import { useState, useEffect } from "react"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Button } from "@/components/ui/button"
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select"
import { Checkbox } from "@/components/ui/checkbox"
import { Badge } from "@/components/ui/badge"
import { Calculator, Loader2, Wifi, WifiOff } from "lucide-react"
import { calcularFolhaPagamento, testarAPI, type FolhaPagamento } from "@/lib/api"
import { toast } from "sonner"

export function PayrollCalculator() {
  const [nome, setNome] = useState("")
  const [cpf, setCpf] = useState("")
  const [cargo, setCargo] = useState("")
  const [salary, setSalary] = useState("")
  const [dependents, setDependents] = useState("0")
  const [periculosidade, setPericulosidade] = useState(false)
  const [insalubridade, setInsalubridade] = useState("")
  const [valeTransporte, setValeTransporte] = useState("0")
  const [valeAlimentacao, setValeAlimentacao] = useState("0")
  const [result, setResult] = useState<FolhaPagamento | null>(null)
  const [loading, setLoading] = useState(false)
  const [apiStatus, setApiStatus] = useState<"checking" | "online" | "offline">("checking")

  useEffect(() => {
    // Testar conexão com API ao montar o componente
    const checkAPI = async () => {
      try {
        await testarAPI()
        setApiStatus("online")
        toast.success("✅ Conectado ao backend!")
      } catch (error) {
        setApiStatus("offline")
        toast.error("⚠️ Backend offline. Inicie o servidor Spring Boot.")
      }
    }
    checkAPI()
  }, [])

  const calculatePayroll = async () => {
    const grossSalary = Number.parseFloat(salary)
    if (isNaN(grossSalary) || grossSalary <= 0) {
      toast.error("Por favor, insira um salário válido")
      return
    }

    if (!nome || !cargo) {
      toast.error("Por favor, preencha nome e cargo")
      return
    }

    setLoading(true)
    try {
      const funcionario = {
        nome,
        cpf: cpf || "000.000.000-00",
        cargo,
        salarioBruto: grossSalary,
        numeroDependentes: Number.parseInt(dependents) || 0,
        recebePericulosidade: periculosidade,
        grauInsalubridade: insalubridade || "",
        valorValeTransporte: Number.parseFloat(valeTransporte) || 0,
        valorValeAlimentacao: Number.parseFloat(valeAlimentacao) || 0,
      }

      console.log("🚀 Enviando para API:", funcionario)
      toast.loading("Calculando via backend...")
      
      const folha = await calcularFolhaPagamento(funcionario)
      
      console.log("✅ Resposta da API:", folha)
      setResult(folha)
      toast.success("✅ Folha calculada pelo backend com sucesso!")
    } catch (error) {
      console.error("❌ Erro ao calcular folha:", error)
      toast.error("❌ Erro ao calcular. Verifique se o backend está rodando na porta 8080.")
      setApiStatus("offline")
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="grid gap-6 lg:grid-cols-2">
      <Card className="bg-card border-border">
        <CardHeader>
          <div className="flex items-center justify-between">
            <CardTitle className="text-foreground flex items-center gap-2">
              <Calculator className="h-5 w-5" />
              Calculadora de Folha
            </CardTitle>
            <Badge
              variant={apiStatus === "online" ? "default" : apiStatus === "offline" ? "destructive" : "secondary"}
              className="flex items-center gap-1"
            >
              {apiStatus === "online" && <Wifi className="h-3 w-3" />}
              {apiStatus === "offline" && <WifiOff className="h-3 w-3" />}
              {apiStatus === "checking" && <Loader2 className="h-3 w-3 animate-spin" />}
              {apiStatus === "online" ? "Backend Online" : apiStatus === "offline" ? "Backend Offline" : "Verificando..."}
            </Badge>
          </div>
          <CardDescription className="text-muted-foreground">
            Calcule a folha de pagamento completa usando a API integrada
          </CardDescription>
        </CardHeader>
        <CardContent className="space-y-4">
          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="nome" className="text-foreground">
                Nome *
              </Label>
              <Input
                id="nome"
                placeholder="Nome do funcionário"
                value={nome}
                onChange={(e) => setNome(e.target.value)}
                className="bg-secondary border-border text-foreground"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="cpf" className="text-foreground">
                CPF
              </Label>
              <Input
                id="cpf"
                placeholder="000.000.000-00"
                value={cpf}
                onChange={(e) => setCpf(e.target.value)}
                className="bg-secondary border-border text-foreground"
              />
            </div>
          </div>

          <div className="space-y-2">
            <Label htmlFor="cargo" className="text-foreground">
              Cargo *
            </Label>
            <Input
              id="cargo"
              placeholder="Ex: Desenvolvedor"
              value={cargo}
              onChange={(e) => setCargo(e.target.value)}
              className="bg-secondary border-border text-foreground"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="salary" className="text-foreground">
              Salário Bruto (R$) *
            </Label>
            <Input
              id="salary"
              type="number"
              placeholder="0.00"
              value={salary}
              onChange={(e) => setSalary(e.target.value)}
              className="bg-secondary border-border text-foreground font-mono text-lg"
            />
          </div>

          <div className="space-y-2">
            <Label htmlFor="dependents" className="text-foreground">
              Número de Dependentes
            </Label>
            <Input
              id="dependents"
              type="number"
              placeholder="0"
              value={dependents}
              onChange={(e) => setDependents(e.target.value)}
              className="bg-secondary border-border text-foreground"
            />
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="valeTransporte" className="text-foreground">
                Vale Transporte (R$)
              </Label>
              <Input
                id="valeTransporte"
                type="number"
                placeholder="0.00"
                value={valeTransporte}
                onChange={(e) => setValeTransporte(e.target.value)}
                className="bg-secondary border-border text-foreground font-mono"
              />
            </div>

            <div className="space-y-2">
              <Label htmlFor="valeAlimentacao" className="text-foreground">
                Vale Alimentação (R$)
              </Label>
              <Input
                id="valeAlimentacao"
                type="number"
                placeholder="0.00"
                value={valeAlimentacao}
                onChange={(e) => setValeAlimentacao(e.target.value)}
                className="bg-secondary border-border text-foreground font-mono"
              />
            </div>
          </div>

          <div className="space-y-3">
            <div className="flex items-center space-x-2">
              <Checkbox
                id="periculosidade"
                checked={periculosidade}
                onCheckedChange={(checked) => setPericulosidade(checked as boolean)}
              />
              <Label htmlFor="periculosidade" className="text-foreground cursor-pointer">
                Recebe adicional de periculosidade (30%)
              </Label>
            </div>

            <div className="space-y-2">
              <Label htmlFor="insalubridade" className="text-foreground">
                Grau de Insalubridade
              </Label>
              <Select value={insalubridade || "NENHUM"} onValueChange={(value) => setInsalubridade(value === "NENHUM" ? "" : value)}>
                <SelectTrigger className="bg-secondary border-border text-foreground">
                  <SelectValue placeholder="Selecione o grau" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="NENHUM">Nenhum</SelectItem>
                  <SelectItem value="MINIMO">Mínimo (10%)</SelectItem>
                  <SelectItem value="MEDIO">Médio (20%)</SelectItem>
                  <SelectItem value="MAXIMO">Máximo (40%)</SelectItem>
                </SelectContent>
              </Select>
            </div>
          </div>

          <Button
            onClick={calculatePayroll}
            disabled={loading}
            className="w-full bg-primary hover:bg-primary/90"
          >
            {loading ? (
              <>
                <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                Calculando...
              </>
            ) : (
              "Calcular Folha"
            )}
          </Button>
        </CardContent>
      </Card>

      {result && (
        <Card className="bg-card border-border">
          <CardHeader>
            <CardTitle className="text-foreground">Resultado do Cálculo</CardTitle>
            <CardDescription className="text-muted-foreground">
              Folha de {result.funcionario.nome}
            </CardDescription>
          </CardHeader>
          <CardContent className="space-y-4">
            <div className="space-y-3">
              {/* Salário Base */}
              <div className="flex items-center justify-between p-3 bg-secondary rounded-lg">
                <span className="text-muted-foreground">Salário Bruto</span>
                <span className="text-lg font-mono font-semibold text-foreground">
                  R$ {result.salarioBruto.toFixed(2)}
                </span>
              </div>

              {/* Adicionais */}
              {(result.adicionalPericulosidade > 0 || result.adicionalInsalubridade > 0) && (
                <div className="space-y-2 p-3 bg-green-500/10 rounded-lg border border-green-500/20">
                  <div className="font-semibold text-sm text-muted-foreground mb-2">Adicionais</div>
                  {result.adicionalPericulosidade > 0 && (
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground text-sm">Periculosidade</span>
                      <span className="font-mono text-green-600 dark:text-green-400">
                        +R$ {result.adicionalPericulosidade.toFixed(2)}
                      </span>
                    </div>
                  )}
                  {result.adicionalInsalubridade > 0 && (
                    <div className="flex items-center justify-between">
                      <span className="text-muted-foreground text-sm">Insalubridade</span>
                      <span className="font-mono text-green-600 dark:text-green-400">
                        +R$ {result.adicionalInsalubridade.toFixed(2)}
                      </span>
                    </div>
                  )}
                </div>
              )}

              {/* Total antes dos descontos */}
              <div className="flex items-center justify-between p-3 bg-blue-500/10 rounded-lg border border-blue-500/20">
                <span className="text-foreground font-medium">Total antes dos descontos</span>
                <span className="text-lg font-mono font-semibold text-blue-600 dark:text-blue-400">
                  R$ {result.totalAntesDescontos.toFixed(2)}
                </span>
              </div>

              {/* Descontos */}
              <div className="space-y-2 p-3 bg-destructive/10 rounded-lg border border-destructive/20">
                <div className="font-semibold text-sm text-muted-foreground mb-2">Descontos</div>
                <div className="flex items-center justify-between">
                  <span className="text-muted-foreground text-sm">INSS</span>
                  <span className="font-mono text-destructive">-R$ {result.descontoINSS.toFixed(2)}</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-muted-foreground text-sm">IRRF</span>
                  <span className="font-mono text-destructive">-R$ {result.descontoIRRF.toFixed(2)}</span>
                </div>
                {result.descontoValeTransporte > 0 && (
                  <div className="flex items-center justify-between">
                    <span className="text-muted-foreground text-sm">Vale Transporte</span>
                    <span className="font-mono text-destructive">
                      -R$ {result.descontoValeTransporte.toFixed(2)}
                    </span>
                  </div>
                )}
                <div className="flex items-center justify-between pt-2 border-t border-destructive/30">
                  <span className="text-muted-foreground font-medium text-sm">Total Descontos</span>
                  <span className="font-mono text-destructive font-semibold">
                    -R$ {result.totalDescontos.toFixed(2)}
                  </span>
                </div>
              </div>

              {/* Benefícios */}
              {result.valeAlimentacao > 0 && (
                <div className="space-y-2 p-3 bg-amber-500/10 rounded-lg border border-amber-500/20">
                  <div className="font-semibold text-sm text-muted-foreground mb-2">Benefícios</div>
                  <div className="flex items-center justify-between">
                    <span className="text-muted-foreground text-sm">Vale Alimentação</span>
                    <span className="font-mono text-amber-600 dark:text-amber-400">
                      R$ {result.valeAlimentacao.toFixed(2)}
                    </span>
                  </div>
                </div>
              )}

              {/* FGTS */}
              <div className="flex items-center justify-between p-3 bg-purple-500/10 rounded-lg border border-purple-500/20">
                <span className="text-muted-foreground">FGTS (8%)</span>
                <span className="font-mono text-purple-600 dark:text-purple-400">
                  R$ {result.fgts.toFixed(2)}
                </span>
              </div>

              {/* Salário Líquido */}
              <div className="flex items-center justify-between p-4 bg-accent/10 rounded-lg border-2 border-accent">
                <span className="text-foreground font-semibold">Salário Líquido</span>
                <span className="text-2xl font-mono font-bold text-accent">
                  R$ {result.salarioLiquido.toFixed(2)}
                </span>
              </div>

              {/* Informações adicionais */}
              <div className="pt-4 space-y-2 text-sm text-muted-foreground border-t">
                <div className="flex items-center justify-between">
                  <span>Salário por hora:</span>
                  <span className="font-mono text-foreground">R$ {result.salarioPorHora.toFixed(2)}/h</span>
                </div>
                <div className="flex items-center justify-between">
                  <span>Percentual de desconto:</span>
                  <span className="font-mono text-foreground">
                    {((result.totalDescontos / result.totalAntesDescontos) * 100).toFixed(2)}%
                  </span>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>
      )}
    </div>
  )
}
