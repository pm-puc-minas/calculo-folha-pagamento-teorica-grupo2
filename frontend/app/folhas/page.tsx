"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { Button } from '@/components/ui/button'
import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardContent,
  CardHeader,
} from '@/components/ui/card'
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from '@/components/ui/table'
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from '@/components/ui/select'
import { 
  Loader2, 
  FileText,
  RefreshCw,
  Eye,
  Calendar,
  TrendingUp,
  TrendingDown
} from 'lucide-react'
import { 
  listarFolhas,
  listarFolhasPorPeriodo,
  type FolhaPagamentoEntity 
} from '@/lib/api'
import { toast } from 'sonner'

const meses = [
  { value: '1', label: 'Janeiro' },
  { value: '2', label: 'Fevereiro' },
  { value: '3', label: 'Março' },
  { value: '4', label: 'Abril' },
  { value: '5', label: 'Maio' },
  { value: '6', label: 'Junho' },
  { value: '7', label: 'Julho' },
  { value: '8', label: 'Agosto' },
  { value: '9', label: 'Setembro' },
  { value: '10', label: 'Outubro' },
  { value: '11', label: 'Novembro' },
  { value: '12', label: 'Dezembro' },
]

export default function FolhasPage() {
  const { isAuthenticated, loading: authLoading } = useAuth()
  const router = useRouter()
  
  const [folhas, setFolhas] = useState<FolhaPagamentoEntity[]>([])
  const [loading, setLoading] = useState(true)
  const [mesFilter, setMesFilter] = useState<string>('todos')
  const [anoFilter, setAnoFilter] = useState<string>('todos')

  const anoAtual = new Date().getFullYear()
  const anos = Array.from({ length: 5 }, (_, i) => (anoAtual - i).toString())

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, authLoading, router])

  useEffect(() => {
    if (isAuthenticated) {
      carregarFolhas()
    }
  }, [isAuthenticated, mesFilter, anoFilter])

  const carregarFolhas = async () => {
    setLoading(true)
    try {
      let data: FolhaPagamentoEntity[]
      
      if (mesFilter !== 'todos' && anoFilter !== 'todos') {
        data = await listarFolhasPorPeriodo(parseInt(mesFilter), parseInt(anoFilter))
      } else {
        data = await listarFolhas()
      }
      
      setFolhas(data)
    } catch (error) {
      toast.error('Erro ao carregar folhas de pagamento')
      console.error(error)
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

  const getMesNome = (mes: number) => {
    return meses.find(m => m.value === mes.toString())?.label || mes.toString()
  }

  // Calcular totais
  const totalBruto = folhas.reduce((acc, f) => acc + f.salarioBruto, 0)
  const totalLiquido = folhas.reduce((acc, f) => acc + f.salarioLiquido, 0)
  const totalDescontos = folhas.reduce((acc, f) => acc + f.totalDescontos, 0)

  if (authLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <Loader2 className="h-12 w-12 animate-spin text-primary" />
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
        <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4 mb-6">
          <div>
            <h2 className="text-2xl font-bold text-foreground flex items-center gap-2">
              <FileText className="h-6 w-6" />
              Folhas de Pagamento
            </h2>
            <p className="text-sm text-muted-foreground">
              Histórico de folhas de pagamento calculadas
            </p>
          </div>
        </div>

        {/* Cards de Resumo */}
        {folhas.length > 0 && (
          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-muted-foreground">Total Bruto</p>
                    <p className="text-2xl font-bold font-mono text-blue-600">
                      {formatCurrency(totalBruto)}
                    </p>
                  </div>
                  <TrendingUp className="h-8 w-8 text-blue-600/20" />
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-muted-foreground">Total Descontos</p>
                    <p className="text-2xl font-bold font-mono text-red-600">
                      {formatCurrency(totalDescontos)}
                    </p>
                  </div>
                  <TrendingDown className="h-8 w-8 text-red-600/20" />
                </div>
              </CardContent>
            </Card>
            
            <Card>
              <CardContent className="pt-6">
                <div className="flex items-center justify-between">
                  <div>
                    <p className="text-sm text-muted-foreground">Total Líquido</p>
                    <p className="text-2xl font-bold font-mono text-green-600">
                      {formatCurrency(totalLiquido)}
                    </p>
                  </div>
                  <FileText className="h-8 w-8 text-green-600/20" />
                </div>
              </CardContent>
            </Card>
          </div>
        )}

        <Card>
          <CardHeader className="pb-4">
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="flex items-center gap-2">
                <Calendar className="h-4 w-4 text-muted-foreground" />
                <span className="text-sm text-muted-foreground">Filtrar por período:</span>
              </div>
              
              <Select value={mesFilter} onValueChange={setMesFilter}>
                <SelectTrigger className="w-full sm:w-[180px]">
                  <SelectValue placeholder="Mês" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos os meses</SelectItem>
                  {meses.map(mes => (
                    <SelectItem key={mes.value} value={mes.value}>{mes.label}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              
              <Select value={anoFilter} onValueChange={setAnoFilter}>
                <SelectTrigger className="w-full sm:w-[140px]">
                  <SelectValue placeholder="Ano" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos os anos</SelectItem>
                  {anos.map(ano => (
                    <SelectItem key={ano} value={ano}>{ano}</SelectItem>
                  ))}
                </SelectContent>
              </Select>
              
              <Button variant="outline" size="icon" onClick={carregarFolhas} className="ml-auto">
                <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
              </Button>
            </div>
          </CardHeader>
          
          <CardContent>
            {loading ? (
              <div className="flex items-center justify-center py-12">
                <Loader2 className="h-8 w-8 animate-spin text-primary" />
              </div>
            ) : folhas.length === 0 ? (
              <div className="text-center py-12">
                <FileText className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <h3 className="text-lg font-medium text-foreground mb-1">
                  Nenhuma folha encontrada
                </h3>
                <p className="text-sm text-muted-foreground mb-4">
                  {mesFilter !== 'todos' || anoFilter !== 'todos'
                    ? 'Tente ajustar os filtros de período'
                    : 'Calcule a folha de um funcionário para começar'
                  }
                </p>
                <Button onClick={() => router.push('/funcionarios')}>
                  Ver Funcionários
                </Button>
              </div>
            ) : (
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Funcionário</TableHead>
                      <TableHead className="hidden sm:table-cell">Referência</TableHead>
                      <TableHead className="text-right">Salário Bruto</TableHead>
                      <TableHead className="text-right hidden md:table-cell">Descontos</TableHead>
                      <TableHead className="text-right">Salário Líquido</TableHead>
                      <TableHead className="text-right">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {folhas.map((folha) => (
                      <TableRow key={folha.id}>
                        <TableCell className="font-medium">
                          <div>
                            {folha.funcionario?.nome || 'N/A'}
                            <Badge 
                              variant={folha.funcionario?.tipo === 'CLT' ? 'default' : 'secondary'}
                              className="ml-2"
                            >
                              {folha.funcionario?.tipo || 'N/A'}
                            </Badge>
                          </div>
                          <div className="text-xs text-muted-foreground sm:hidden">
                            {getMesNome(folha.mesReferencia)}/{folha.anoReferencia}
                          </div>
                        </TableCell>
                        <TableCell className="hidden sm:table-cell text-muted-foreground">
                          {getMesNome(folha.mesReferencia)}/{folha.anoReferencia}
                        </TableCell>
                        <TableCell className="text-right font-mono">
                          {formatCurrency(folha.salarioBruto)}
                        </TableCell>
                        <TableCell className="text-right font-mono text-destructive hidden md:table-cell">
                          -{formatCurrency(folha.totalDescontos)}
                        </TableCell>
                        <TableCell className="text-right font-mono font-medium text-green-600">
                          {formatCurrency(folha.salarioLiquido)}
                        </TableCell>
                        <TableCell className="text-right">
                          <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => router.push(`/folhas/${folha.id}`)}
                            title="Ver detalhes"
                          >
                            <Eye className="h-4 w-4" />
                          </Button>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
            
            {folhas.length > 0 && (
              <div className="mt-4 text-sm text-muted-foreground">
                Mostrando {folhas.length} folha(s) de pagamento
              </div>
            )}
          </CardContent>
        </Card>
      </main>
    </div>
  )
}

