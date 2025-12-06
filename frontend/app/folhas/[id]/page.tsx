"use client"

import { useEffect, useState } from 'react'
import { useRouter, useParams } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { 
  Loader2,
  ArrowLeft,
  FileText,
  User,
  Briefcase,
  Calendar,
  Printer
} from 'lucide-react'
import { buscarFolha, type FolhaPagamentoEntity } from '@/lib/api'
import { toast } from 'sonner'

const meses = [
  'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
  'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
]

export default function FolhaDetalhesPage() {
  const { isAuthenticated, loading: authLoading } = useAuth()
  const router = useRouter()
  const params = useParams()
  const id = Number(params.id)
  
  const [folha, setFolha] = useState<FolhaPagamentoEntity | null>(null)
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login')
      return
    }
    
    if (isAuthenticated && id) {
      carregarFolha()
    }
  }, [isAuthenticated, authLoading, id])

  const carregarFolha = async () => {
    try {
      const data = await buscarFolha(id)
      setFolha(data)
    } catch (error) {
      toast.error('Folha não encontrada')
      router.push('/folhas')
    } finally {
      setLoading(false)
    }
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value)
  }

  const formatDate = (dateString: string) => {
    return new Date(dateString).toLocaleDateString('pt-BR', {
      day: '2-digit',
      month: '2-digit',
      year: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    })
  }

  const handlePrint = () => {
    window.print()
  }

  if (authLoading || loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <Loader2 className="h-12 w-12 animate-spin text-primary" />
      </div>
    )
  }

  if (!isAuthenticated || !folha) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      <main className="container mx-auto px-4 py-6 max-w-4xl">
        <div className="flex items-center justify-between mb-6">
          <Button variant="ghost" onClick={() => router.push('/folhas')} className="gap-2">
            <ArrowLeft className="h-4 w-4" />
            Voltar
          </Button>
          
          <Button variant="outline" onClick={handlePrint} className="gap-2 print:hidden">
            <Printer className="h-4 w-4" />
            Imprimir
          </Button>
        </div>

        <Card className="mb-6">
          <CardHeader>
            <div className="flex items-center justify-between">
              <div>
                <CardTitle className="flex items-center gap-2">
                  <FileText className="h-5 w-5" />
                  Folha de Pagamento
                </CardTitle>
                <CardDescription>
                  Referência: {meses[folha.mesReferencia - 1]}/{folha.anoReferencia}
                </CardDescription>
              </div>
              <Badge variant="outline" className="text-lg px-4 py-2">
                #{folha.id}
              </Badge>
            </div>
          </CardHeader>
        </Card>

        {/* Dados do Funcionário */}
        <Card className="mb-6">
          <CardHeader className="pb-3">
            <CardTitle className="text-base flex items-center gap-2">
              <User className="h-4 w-4" />
              Dados do Funcionário
            </CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              <div>
                <p className="text-sm text-muted-foreground">Nome</p>
                <p className="font-medium">{folha.funcionario?.nome}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">CPF</p>
                <p className="font-medium">{folha.funcionario?.cpf || '-'}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Cargo</p>
                <p className="font-medium">{folha.funcionario?.cargo}</p>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Tipo</p>
                <Badge variant={folha.funcionario?.tipo === 'CLT' ? 'default' : 'secondary'}>
                  {folha.funcionario?.tipo}
                </Badge>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Detalhes da Folha */}
        <Card>
          <CardHeader className="pb-3">
            <CardTitle className="text-base flex items-center gap-2">
              <Briefcase className="h-4 w-4" />
              Detalhes do Cálculo
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4">
            {/* Salário Base */}
            <div className="flex items-center justify-between p-3 bg-secondary rounded-lg">
              <span className="text-muted-foreground">Salário Bruto</span>
              <span className="text-lg font-mono font-semibold">
                {formatCurrency(folha.salarioBruto)}
              </span>
            </div>

            {/* Adicionais */}
            {(folha.adicionalPericulosidade > 0 || folha.adicionalInsalubridade > 0) && (
              <div className="space-y-2 p-3 bg-green-500/10 rounded-lg border border-green-500/20">
                <div className="font-semibold text-sm text-muted-foreground mb-2">Adicionais</div>
                {folha.adicionalPericulosidade > 0 && (
                  <div className="flex items-center justify-between">
                    <span className="text-muted-foreground text-sm">Periculosidade (30%)</span>
                    <span className="font-mono text-green-600 dark:text-green-400">
                      +{formatCurrency(folha.adicionalPericulosidade)}
                    </span>
                  </div>
                )}
                {folha.adicionalInsalubridade > 0 && (
                  <div className="flex items-center justify-between">
                    <span className="text-muted-foreground text-sm">Insalubridade</span>
                    <span className="font-mono text-green-600 dark:text-green-400">
                      +{formatCurrency(folha.adicionalInsalubridade)}
                    </span>
                  </div>
                )}
              </div>
            )}

            {/* Total antes dos descontos */}
            <div className="flex items-center justify-between p-3 bg-blue-500/10 rounded-lg border border-blue-500/20">
              <span className="font-medium">Total antes dos descontos</span>
              <span className="text-lg font-mono font-semibold text-blue-600 dark:text-blue-400">
                {formatCurrency(folha.totalAntesDescontos)}
              </span>
            </div>

            {/* Descontos */}
            <div className="space-y-2 p-3 bg-destructive/10 rounded-lg border border-destructive/20">
              <div className="font-semibold text-sm text-muted-foreground mb-2">Descontos</div>
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground text-sm">INSS</span>
                <span className="font-mono text-destructive">-{formatCurrency(folha.descontoINSS)}</span>
              </div>
              <div className="flex items-center justify-between">
                <span className="text-muted-foreground text-sm">IRRF</span>
                <span className="font-mono text-destructive">-{formatCurrency(folha.descontoIRRF)}</span>
              </div>
              {folha.descontoValeTransporte > 0 && (
                <div className="flex items-center justify-between">
                  <span className="text-muted-foreground text-sm">Vale Transporte (6%)</span>
                  <span className="font-mono text-destructive">
                    -{formatCurrency(folha.descontoValeTransporte)}
                  </span>
                </div>
              )}
              <div className="flex items-center justify-between pt-2 border-t border-destructive/30">
                <span className="font-medium text-sm">Total Descontos</span>
                <span className="font-mono text-destructive font-semibold">
                  -{formatCurrency(folha.totalDescontos)}
                </span>
              </div>
            </div>

            {/* Benefícios */}
            {folha.valeAlimentacao > 0 && (
              <div className="space-y-2 p-3 bg-amber-500/10 rounded-lg border border-amber-500/20">
                <div className="font-semibold text-sm text-muted-foreground mb-2">Benefícios</div>
                <div className="flex items-center justify-between">
                  <span className="text-muted-foreground text-sm">Vale Alimentação</span>
                  <span className="font-mono text-amber-600 dark:text-amber-400">
                    {formatCurrency(folha.valeAlimentacao)}
                  </span>
                </div>
              </div>
            )}

            {/* FGTS */}
            <div className="flex items-center justify-between p-3 bg-purple-500/10 rounded-lg border border-purple-500/20">
              <span className="text-muted-foreground">FGTS (8%) - Depósito</span>
              <span className="font-mono text-purple-600 dark:text-purple-400">
                {formatCurrency(folha.fgts)}
              </span>
            </div>

            {/* Salário Líquido */}
            <div className="flex items-center justify-between p-4 bg-primary/10 rounded-lg border-2 border-primary">
              <span className="font-semibold text-lg">Salário Líquido</span>
              <span className="text-2xl font-mono font-bold text-primary">
                {formatCurrency(folha.salarioLiquido)}
              </span>
            </div>

            {/* Informações adicionais */}
            <div className="pt-4 space-y-2 text-sm text-muted-foreground border-t">
              <div className="flex items-center justify-between">
                <span>Salário por hora:</span>
                <span className="font-mono">{formatCurrency(folha.salarioPorHora)}/h</span>
              </div>
              <div className="flex items-center justify-between">
                <span>Percentual de desconto:</span>
                <span className="font-mono">
                  {((folha.totalDescontos / folha.totalAntesDescontos) * 100).toFixed(2)}%
                </span>
              </div>
              <div className="flex items-center justify-between">
                <span className="flex items-center gap-1">
                  <Calendar className="h-3 w-3" />
                  Data do cálculo:
                </span>
                <span>{formatDate(folha.dataCalculo)}</span>
              </div>
            </div>
          </CardContent>
        </Card>
      </main>
    </div>
  )
}

