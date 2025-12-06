"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { Button } from '@/components/ui/button'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
} from '@/components/ui/card'
import { 
  Loader2, 
  Users, 
  Briefcase, 
  FileText,
  TrendingUp,
  Calculator,
  Plus,
  ArrowRight
} from 'lucide-react'
import { 
  obterEstatisticasFuncionarios, 
  agruparFuncionariosPorTipo,
  listarFolhas,
  type EstatisticasFuncionarios,
  type FuncionarioEntity,
  type FolhaPagamentoEntity
} from '@/lib/api'
import { toast } from 'sonner'
import Link from 'next/link'

export default function DashboardPage() {
  const { isAuthenticated, loading: authLoading, user } = useAuth()
  const router = useRouter()
  
  const [loading, setLoading] = useState(true)
  const [stats, setStats] = useState<EstatisticasFuncionarios | null>(null)
  const [funcionariosPorTipo, setFuncionariosPorTipo] = useState<Record<string, FuncionarioEntity[]>>({})
  const [folhasRecentes, setFolhasRecentes] = useState<FolhaPagamentoEntity[]>([])

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, authLoading, router])

  useEffect(() => {
    if (isAuthenticated) {
      carregarDados()
    }
  }, [isAuthenticated])

  const carregarDados = async () => {
    setLoading(true)
    try {
      const [statsData, tiposData, folhasData] = await Promise.all([
        obterEstatisticasFuncionarios().catch(() => null),
        agruparFuncionariosPorTipo().catch(() => ({})),
        listarFolhas().catch(() => [])
      ])
      
      if (statsData) setStats(statsData)
      setFuncionariosPorTipo(tiposData)
      setFolhasRecentes(folhasData.slice(0, 5))
    } catch (error) {
      console.error('Erro ao carregar dados:', error)
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

  const totalCLT = funcionariosPorTipo['CLT']?.length || 0
  const totalPJ = funcionariosPorTipo['PJ']?.length || 0

  if (authLoading) {
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
        {/* Boas-vindas */}
        <div className="mb-8">
          <h2 className="text-2xl font-bold text-foreground mb-1.5">
            Olá, {user?.nome?.split(' ')[0] || 'Usuário'}! 👋
          </h2>
          <p className="text-sm text-muted-foreground">
            Bem-vindo ao Sistema de Gestão de Folha de Pagamento
          </p>
        </div>

        {loading ? (
          <div className="flex items-center justify-center py-12">
            <Loader2 className="h-8 w-8 animate-spin text-primary" />
          </div>
        ) : (
          <>
            {/* Cards de Estatísticas */}
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4 mb-8">
              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-muted-foreground">Total Funcionários</p>
                      <p className="text-3xl font-bold">{stats?.totalFuncionarios || 0}</p>
                    </div>
                    <div className="h-12 w-12 rounded-full bg-blue-500/10 flex items-center justify-center">
                      <Users className="h-6 w-6 text-blue-500" />
                    </div>
                  </div>
                  <div className="mt-4 flex gap-2 text-xs">
                    <span className="px-2 py-1 rounded bg-primary/10 text-primary font-medium">
                      {totalCLT} CLT
                    </span>
                    <span className="px-2 py-1 rounded bg-secondary text-secondary-foreground font-medium">
                      {totalPJ} PJ
                    </span>
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-muted-foreground">Salário Médio</p>
                      <p className="text-2xl font-bold font-mono">
                        {formatCurrency(stats?.salarioMedio || 0)}
                      </p>
                    </div>
                    <div className="h-12 w-12 rounded-full bg-green-500/10 flex items-center justify-center">
                      <TrendingUp className="h-6 w-6 text-green-500" />
                    </div>
                  </div>
                  <div className="mt-4 text-xs text-muted-foreground">
                    Min: {formatCurrency(stats?.salarioMinimo || 0)} | 
                    Max: {formatCurrency(stats?.salarioMaximo || 0)}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-muted-foreground">Folhas Geradas</p>
                      <p className="text-3xl font-bold">{folhasRecentes.length > 0 ? folhasRecentes.length + '+' : 0}</p>
                    </div>
                    <div className="h-12 w-12 rounded-full bg-purple-500/10 flex items-center justify-center">
                      <FileText className="h-6 w-6 text-purple-500" />
                    </div>
                  </div>
                  <div className="mt-4 text-xs text-muted-foreground">
                    {folhasRecentes.length > 0 ? 'Últimas folhas calculadas' : 'Nenhuma folha calculada'}
                  </div>
                </CardContent>
              </Card>

              <Card>
                <CardContent className="pt-6">
                  <div className="flex items-center justify-between">
                    <div>
                      <p className="text-sm text-muted-foreground">Com Adicionais</p>
                      <p className="text-3xl font-bold">
                        {(stats?.funcionariosComPericulosidade || 0) + (stats?.funcionariosComInsalubridade || 0)}
                      </p>
                    </div>
                    <div className="h-12 w-12 rounded-full bg-amber-500/10 flex items-center justify-center">
                      <Briefcase className="h-6 w-6 text-amber-500" />
                    </div>
                  </div>
                  <div className="mt-4 text-xs text-muted-foreground">
                    {stats?.funcionariosComPericulosidade || 0} periculosidade | 
                    {stats?.funcionariosComInsalubridade || 0} insalubridade
                  </div>
                </CardContent>
              </Card>
            </div>

            {/* Ações Rápidas e Folhas Recentes */}
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              {/* Ações Rápidas */}
              <Card>
                <CardHeader>
                  <CardTitle className="text-lg">Ações Rápidas</CardTitle>
                  <CardDescription>Acesso rápido às principais funcionalidades</CardDescription>
                </CardHeader>
                <CardContent className="space-y-3">
                  <Link href="/funcionarios/novo">
                    <Button variant="outline" className="w-full justify-start gap-3 h-auto py-4">
                      <div className="h-10 w-10 rounded-lg bg-blue-500/10 flex items-center justify-center">
                        <Plus className="h-5 w-5 text-blue-500" />
                      </div>
                      <div className="text-left">
                        <p className="font-medium">Cadastrar Funcionário</p>
                        <p className="text-xs text-muted-foreground">Adicione um novo funcionário ao sistema</p>
                      </div>
                    </Button>
                  </Link>
                  
                  <Link href="/funcionarios">
                    <Button variant="outline" className="w-full justify-start gap-3 h-auto py-4">
                      <div className="h-10 w-10 rounded-lg bg-green-500/10 flex items-center justify-center">
                        <Calculator className="h-5 w-5 text-green-500" />
                      </div>
                      <div className="text-left">
                        <p className="font-medium">Calcular Folha</p>
                        <p className="text-xs text-muted-foreground">Gere folha de pagamento para funcionários</p>
                      </div>
                    </Button>
                  </Link>
                  
                  <Link href="/folhas">
                    <Button variant="outline" className="w-full justify-start gap-3 h-auto py-4">
                      <div className="h-10 w-10 rounded-lg bg-purple-500/10 flex items-center justify-center">
                        <FileText className="h-5 w-5 text-purple-500" />
                      </div>
                      <div className="text-left">
                        <p className="font-medium">Ver Histórico</p>
                        <p className="text-xs text-muted-foreground">Consulte as folhas de pagamento geradas</p>
                      </div>
                    </Button>
                  </Link>
                </CardContent>
              </Card>

              {/* Folhas Recentes */}
              <Card>
                <CardHeader className="flex flex-row items-center justify-between">
                  <div>
                    <CardTitle className="text-lg">Folhas Recentes</CardTitle>
                    <CardDescription>Últimas folhas de pagamento calculadas</CardDescription>
                  </div>
                  <Link href="/folhas">
                    <Button variant="ghost" size="sm" className="gap-1">
                      Ver todas
                      <ArrowRight className="h-4 w-4" />
                    </Button>
                  </Link>
                </CardHeader>
                <CardContent>
                  {folhasRecentes.length === 0 ? (
                    <div className="text-center py-8 text-muted-foreground">
                      <FileText className="h-10 w-10 mx-auto mb-3 opacity-50" />
                      <p>Nenhuma folha calculada ainda</p>
                      <Link href="/funcionarios">
                        <Button variant="link" className="mt-2">
                          Calcular primeira folha
                        </Button>
                      </Link>
                    </div>
                  ) : (
                    <div className="space-y-3">
                      {folhasRecentes.map((folha) => (
                        <Link key={folha.id} href={`/folhas/${folha.id}`}>
                          <div className="flex items-center justify-between p-3 rounded-lg border hover:bg-accent transition-colors">
                            <div>
                              <p className="font-medium">{folha.funcionario?.nome}</p>
                              <p className="text-xs text-muted-foreground">
                                {['Jan', 'Fev', 'Mar', 'Abr', 'Mai', 'Jun', 'Jul', 'Ago', 'Set', 'Out', 'Nov', 'Dez'][folha.mesReferencia - 1]}/{folha.anoReferencia}
                              </p>
                            </div>
                            <div className="text-right">
                              <p className="font-mono font-medium text-green-600">
                                {formatCurrency(folha.salarioLiquido)}
                              </p>
                              <p className="text-xs text-muted-foreground">Líquido</p>
                            </div>
                          </div>
                        </Link>
                      ))}
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </>
        )}
      </main>
    </div>
  )
}
