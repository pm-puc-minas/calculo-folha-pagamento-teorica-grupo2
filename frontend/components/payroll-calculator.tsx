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
import { calcularFolhaPagamento, testarAPI, APIError, type FolhaPagamento } from "@/lib/api"
import { toast } from "sonner"

interface FieldErrors {
  nome?: string;
  cargo?: string;
  salary?: string;
  dependents?: string;
  valeTransporte?: string;
  valeAlimentacao?: string;
}

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
  const [fieldErrors, setFieldErrors] = useState<FieldErrors>({})

  useEffect(() => {
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

  const validateForm = (): boolean => {
    const errors: FieldErrors = {};
    let isValid = true;

    if (!nome || nome.trim().length < 3) {
      errors.nome = "Nome deve ter no mínimo 3 caracteres";
      isValid = false;
    }

    if (!cargo || cargo.trim().length < 2) {
      errors.cargo = "Cargo deve ter no mínimo 2 caracteres";
      isValid = false;
    }

    const grossSalary = Number.parseFloat(salary);
    if (!salary || isNaN(grossSalary)) {
      errors.salary = "Salário é obrigatório";
      isValid = false;
    } else if (grossSalary <= 0) {
      errors.salary = "Salário deve ser maior que zero";
      isValid = false;
    } else if (grossSalary < 1320) {
      errors.salary = "Salário não pode ser menor que R$ 1.320,00";
      isValid = false;
    } else if (grossSalary > 100000) {
      errors.salary = "Salário não pode exceder R$ 100.000,00";
      isValid = false;
    }

    // Validar dependentes
    const numDep = Number.parseInt(dependents);
    if (isNaN(numDep) || numDep < 0) {
      errors.dependents = "Número de dependentes inválido";
      isValid = false;
    } else if (numDep > 20) {
      errors.dependents = "Número de dependentes não pode exceder 20";
      isValid = false;
    }

    const vt = Number.parseFloat(valeTransporte);
    if (isNaN(vt) || vt < 0) {
      errors.valeTransporte = "Valor inválido";
      isValid = false;
    }

    const va = Number.parseFloat(valeAlimentacao);
    if (isNaN(va) || va < 0) {
      errors.valeAlimentacao = "Valor inválido";
      isValid = false;
    }

    setFieldErrors(errors);
    return isValid;
  };

  const calculatePayroll = async () => {
    setFieldErrors({});
    
    if (!validateForm()) {
      toast.error("Por favor, corrija os erros no formulário");
      return;
    }

    const grossSalary = Number.parseFloat(salary);

    setLoading(true);
    try {
      const funcionario = {
        nome: nome.trim(),
        cpf: cpf.trim() || "000.000.000-00",
        cargo: cargo.trim(),
        salarioBruto: grossSalary,
        numeroDependentes: Number.parseInt(dependents) || 0,
        recebePericulosidade: periculosidade,
        grauInsalubridade: insalubridade || "",
        valorValeTransporte: Number.parseFloat(valeTransporte) || 0,
        valorValeAlimentacao: Number.parseFloat(valeAlimentacao) || 0,
      };

      console.log("🚀 Enviando para API:", funcionario);
      toast.loading("Calculando via backend...");
      
      const folha = await calcularFolhaPagamento(funcionario);
      
      console.log("✅ Resposta da API:", folha);
      setResult(folha);
      toast.dismiss();
      toast.success("✅ Folha calculada com sucesso!");
    } catch (error) {
      console.error("❌ Erro ao calcular folha:", error);
      toast.dismiss();
      
      if (error instanceof APIError) {
        if (error.isValidationError()) {
          toast.error(`❌ Dados inválidos\n\n${error.details?.join("\n") || error.message}`, {
            duration: 6000,
          });
        } else if (error.isSalarioError()) {
          toast.error(`❌ ${error.message}`, { duration: 5000 });
          setFieldErrors({ salary: error.message });
        } else if (error.isDependentesError()) {
          toast.error(`❌ ${error.message}`, { duration: 5000 });
          setFieldErrors({ dependents: error.message });
        } else if (error.isFuncionarioError()) {
          toast.error(`❌ ${error.message}`, { duration: 5000 });
        } else if (error.isCalculoError()) {
          toast.error(`❌ Erro no cálculo: ${error.message}`, { duration: 6000 });
        } else if (error.isServerError()) {
          toast.error("❌ Erro no servidor. Tente novamente em alguns instantes.");
          setApiStatus("offline");
        } else {
          toast.error(`❌ ${error.message}`);
        }
      } else if (error instanceof Error) {
        toast.error(`❌ ${error.message}`, { duration: 5000 });
        
        if (error.message.includes('conectar') || error.message.includes('Backend offline')) {
          setApiStatus("offline");
        }
      } else {
        toast.error("❌ Erro inesperado. Tente novamente.");
      }
    } finally {
      setLoading(false);
    }
  };

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
                onChange={(e) => {
                  setNome(e.target.value);
                  if (fieldErrors.nome) setFieldErrors({...fieldErrors, nome: undefined});
                }}
                className={`bg-secondary border-border text-foreground ${fieldErrors.nome ? 'border-red-500 focus-visible:ring-red-500' : ''}`}
              />
              {fieldErrors.nome && (
                <p className="text-sm text-red-500">{fieldErrors.nome}</p>
              )}
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
              onChange={(e) => {
                setCargo(e.target.value);
                if (fieldErrors.cargo) setFieldErrors({...fieldErrors, cargo: undefined});
              }}
              className={`bg-secondary border-border text-foreground ${fieldErrors.cargo ? 'border-red-500 focus-visible:ring-red-500' : ''}`}
            />
            {fieldErrors.cargo && (
              <p className="text-sm text-red-500">{fieldErrors.cargo}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="salary" className="text-foreground">
              Salário Bruto (R$) *
            </Label>
            <Input
              id="salary"
              type="text"
              inputMode="decimal"
              placeholder="Ex: 5000.00"
              value={salary}
              onChange={(e) => {
                const value = e.target.value;
                // Permite apenas números, ponto e vírgula
                if (value === '' || /^[0-9.,]*$/.test(value)) {
                  setSalary(value);
                  if (fieldErrors.salary) setFieldErrors({...fieldErrors, salary: undefined});
                }
              }}
              onBlur={(e) => {
                // Formata o valor ao sair do campo
                const value = e.target.value.replace(',', '.');
                const num = parseFloat(value);
                if (!isNaN(num)) {
                  setSalary(num.toString());
                }
              }}
              className={`bg-secondary border-border text-foreground font-mono text-lg ${fieldErrors.salary ? 'border-red-500 focus-visible:ring-red-500' : ''}`}
            />
            {fieldErrors.salary && (
              <p className="text-sm text-red-500">{fieldErrors.salary}</p>
            )}
          </div>

          <div className="space-y-2">
            <Label htmlFor="dependents" className="text-foreground">
              Número de Dependentes
            </Label>
            <Input
              id="dependents"
              type="text"
              inputMode="numeric"
              placeholder="Ex: 2"
              value={dependents}
              onChange={(e) => {
                const value = e.target.value;
                if (value === '' || /^[0-9]*$/.test(value)) {
                  setDependents(value);
                  if (fieldErrors.dependents) setFieldErrors({...fieldErrors, dependents: undefined});
                }
              }}
              className={`bg-secondary border-border text-foreground ${fieldErrors.dependents ? 'border-red-500 focus-visible:ring-red-500' : ''}`}
            />
            {fieldErrors.dependents && (
              <p className="text-sm text-red-500">{fieldErrors.dependents}</p>
            )}
          </div>

          <div className="grid grid-cols-2 gap-4">
            <div className="space-y-2">
              <Label htmlFor="valeTransporte" className="text-foreground">
                Vale Transporte Mensal (R$)
              </Label>
              <Input
                id="valeTransporte"
                type="text"
                inputMode="decimal"
                placeholder="Ex: 200.00"
                value={valeTransporte}
                onChange={(e) => {
                  const value = e.target.value;
                  if (value === '' || /^[0-9.,]*$/.test(value)) {
                    setValeTransporte(value);
                    if (fieldErrors.valeTransporte) setFieldErrors({...fieldErrors, valeTransporte: undefined});
                  }
                }}
                onBlur={(e) => {
                  const value = e.target.value.replace(',', '.');
                  const num = parseFloat(value);
                  if (!isNaN(num)) {
                    setValeTransporte(num.toString());
                  }
                }}
                className={`bg-secondary border-border text-foreground font-mono ${fieldErrors.valeTransporte ? 'border-red-500 focus-visible:ring-red-500' : ''}`}
              />
              {fieldErrors.valeTransporte && (
                <p className="text-sm text-red-500">{fieldErrors.valeTransporte}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="valeAlimentacao" className="text-foreground">
                Vale Alimentação Mensal (R$)
              </Label>
              <Input
                id="valeAlimentacao"
                type="text"
                inputMode="decimal"
                placeholder="Ex: 1200.00"
                value={valeAlimentacao}
                onChange={(e) => {
                  const value = e.target.value;
                  if (value === '' || /^[0-9.,]*$/.test(value)) {
                    setValeAlimentacao(value);
                    if (fieldErrors.valeAlimentacao) setFieldErrors({...fieldErrors, valeAlimentacao: undefined});
                  }
                }}
                onBlur={(e) => {
                  const value = e.target.value.replace(',', '.');
                  const num = parseFloat(value);
                  if (!isNaN(num)) {
                    setValeAlimentacao(num.toString());
                  }
                }}
                className={`bg-secondary border-border text-foreground font-mono ${fieldErrors.valeAlimentacao ? 'border-red-500 focus-visible:ring-red-500' : ''}`}
              />
              {fieldErrors.valeAlimentacao && (
                <p className="text-sm text-red-500">{fieldErrors.valeAlimentacao}</p>
              )}
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
