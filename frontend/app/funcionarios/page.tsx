"use client"

import { useEffect, useState } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { Button } from '@/components/ui/button'
import { Input } from '@/components/ui/input'
import { Badge } from '@/components/ui/badge'
import {
  Card,
  CardContent,
  CardDescription,
  CardHeader,
  CardTitle,
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
  AlertDialog,
  AlertDialogAction,
  AlertDialogCancel,
  AlertDialogContent,
  AlertDialogDescription,
  AlertDialogFooter,
  AlertDialogHeader,
  AlertDialogTitle,
} from '@/components/ui/alert-dialog'
import { 
  Loader2, 
  Plus, 
  Search, 
  Pencil, 
  Trash2, 
  Calculator,
  Users,
  RefreshCw
} from 'lucide-react'
import { 
  listarFuncionarios, 
  excluirFuncionario,
  calcularESalvarFolha,
  APIError,
  type FuncionarioEntity 
} from '@/lib/api'
import { toast } from 'sonner'

export default function FuncionariosPage() {
  const { isAuthenticated, loading: authLoading } = useAuth()
  const router = useRouter()
  
  const [funcionarios, setFuncionarios] = useState<FuncionarioEntity[]>([])
  const [filteredFuncionarios, setFilteredFuncionarios] = useState<FuncionarioEntity[]>([])
  const [loading, setLoading] = useState(true)
  const [searchTerm, setSearchTerm] = useState('')
  const [tipoFilter, setTipoFilter] = useState<string>('todos')
  const [deleteId, setDeleteId] = useState<number | null>(null)
  const [calculatingId, setCalculatingId] = useState<number | null>(null)

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login')
    }
  }, [isAuthenticated, authLoading, router])

  useEffect(() => {
    if (isAuthenticated) {
      carregarFuncionarios()
    }
  }, [isAuthenticated])

  useEffect(() => {
    let filtered = funcionarios

    if (searchTerm) {
      const term = searchTerm.toLowerCase()
      filtered = filtered.filter(f => 
        f.nome.toLowerCase().includes(term) ||
        f.cargo.toLowerCase().includes(term) ||
        f.cpf?.includes(term)
      )
    }

    if (tipoFilter !== 'todos') {
      filtered = filtered.filter(f => f.tipo === tipoFilter)
    }

    setFilteredFuncionarios(filtered)
  }, [funcionarios, searchTerm, tipoFilter])

  const carregarFuncionarios = async () => {
    setLoading(true)
    try {
      const data = await listarFuncionarios()
      setFuncionarios(data)
    } catch (error) {
      toast.error('Erro ao carregar funcionários')
      console.error(error)
    } finally {
      setLoading(false)
    }
  }

  const handleDelete = async () => {
    if (!deleteId) return
    
    try {
      await excluirFuncionario(deleteId)
      toast.success('Funcionário excluído com sucesso!')
      carregarFuncionarios()
    } catch (error) {
      toast.error('Erro ao excluir funcionário')
      console.error(error)
    } finally {
      setDeleteId(null)
    }
  }

  const handleCalcularFolha = async (funcionarioId: number, nome: string) => {
    setCalculatingId(funcionarioId)
    try {
      const folha = await calcularESalvarFolha(funcionarioId)
      toast.success(`Folha de ${nome} calculada e salva!`, {
        action: {
          label: 'Ver detalhes',
          onClick: () => router.push(`/folhas/${folha.id}`)
        }
      })
    } catch (error) {
      if (error instanceof APIError) {
        toast.error(error.message, {
          description: error.isValidationError() 
            ? 'Verifique os dados do funcionário e tente novamente'
            : undefined,
          duration: 5000
        })
      } else {
        toast.error('Erro ao calcular folha de pagamento')
      }
      console.error(error)
    } finally {
      setCalculatingId(null)
    }
  }

  const formatCurrency = (value: number) => {
    return new Intl.NumberFormat('pt-BR', {
      style: 'currency',
      currency: 'BRL'
    }).format(value)
  }

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
              <Users className="h-6 w-6" />
              Funcionários
            </h2>
            <p className="text-sm text-muted-foreground">
              Gerencie os funcionários cadastrados no sistema
            </p>
          </div>
          
          <Button onClick={() => router.push('/funcionarios/novo')} className="gap-2">
            <Plus className="h-4 w-4" />
            Novo Funcionário
          </Button>
        </div>

        <Card>
          <CardHeader className="pb-4">
            <div className="flex flex-col sm:flex-row gap-4">
              <div className="relative flex-1">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 h-4 w-4 text-muted-foreground" />
                <Input
                  placeholder="Buscar por nome, cargo ou CPF..."
                  value={searchTerm}
                  onChange={(e) => setSearchTerm(e.target.value)}
                  className="pl-9"
                />
              </div>
              
              <Select value={tipoFilter} onValueChange={setTipoFilter}>
                <SelectTrigger className="w-full sm:w-[180px]">
                  <SelectValue placeholder="Filtrar por tipo" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="todos">Todos os tipos</SelectItem>
                  <SelectItem value="CLT">CLT</SelectItem>
                  <SelectItem value="PJ">PJ</SelectItem>
                </SelectContent>
              </Select>
              
              <Button variant="outline" size="icon" onClick={carregarFuncionarios}>
                <RefreshCw className={`h-4 w-4 ${loading ? 'animate-spin' : ''}`} />
              </Button>
            </div>
          </CardHeader>
          
          <CardContent>
            {loading ? (
              <div className="flex items-center justify-center py-12">
                <Loader2 className="h-8 w-8 animate-spin text-primary" />
              </div>
            ) : filteredFuncionarios.length === 0 ? (
              <div className="text-center py-12">
                <Users className="h-12 w-12 mx-auto text-muted-foreground mb-4" />
                <h3 className="text-lg font-medium text-foreground mb-1">
                  Nenhum funcionário encontrado
                </h3>
                <p className="text-sm text-muted-foreground mb-4">
                  {searchTerm || tipoFilter !== 'todos' 
                    ? 'Tente ajustar os filtros de busca'
                    : 'Cadastre o primeiro funcionário para começar'
                  }
                </p>
                {!searchTerm && tipoFilter === 'todos' && (
                  <Button onClick={() => router.push('/funcionarios/novo')}>
                    <Plus className="h-4 w-4 mr-2" />
                    Cadastrar Funcionário
                  </Button>
                )}
              </div>
            ) : (
              <div className="rounded-md border">
                <Table>
                  <TableHeader>
                    <TableRow>
                      <TableHead>Nome</TableHead>
                      <TableHead className="hidden sm:table-cell">CPF</TableHead>
                      <TableHead>Cargo</TableHead>
                      <TableHead className="hidden md:table-cell">Tipo</TableHead>
                      <TableHead className="text-right">Salário</TableHead>
                      <TableHead className="text-right">Ações</TableHead>
                    </TableRow>
                  </TableHeader>
                  <TableBody>
                    {filteredFuncionarios.map((funcionario) => (
                      <TableRow key={funcionario.id}>
                        <TableCell className="font-medium">
                          <div>
                            {funcionario.nome}
                            <Badge 
                              variant={funcionario.tipo === 'CLT' ? 'default' : 'secondary'}
                              className="ml-2 md:hidden"
                            >
                              {funcionario.tipo}
                            </Badge>
                          </div>
                        </TableCell>
                        <TableCell className="hidden sm:table-cell text-muted-foreground">
                          {funcionario.cpf || '-'}
                        </TableCell>
                        <TableCell>{funcionario.cargo}</TableCell>
                        <TableCell className="hidden md:table-cell">
                          <Badge variant={funcionario.tipo === 'CLT' ? 'default' : 'secondary'}>
                            {funcionario.tipo}
                          </Badge>
                        </TableCell>
                        <TableCell className="text-right font-mono">
                          {formatCurrency(funcionario.salarioBruto)}
                        </TableCell>
                        <TableCell className="text-right">
                          <div className="flex justify-end gap-1">
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => handleCalcularFolha(funcionario.id, funcionario.nome)}
                              disabled={calculatingId === funcionario.id}
                              title="Calcular Folha"
                            >
                              {calculatingId === funcionario.id ? (
                                <Loader2 className="h-4 w-4 animate-spin" />
                              ) : (
                                <Calculator className="h-4 w-4" />
                              )}
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => router.push(`/funcionarios/${funcionario.id}/editar`)}
                              title="Editar"
                            >
                              <Pencil className="h-4 w-4" />
                            </Button>
                            <Button
                              variant="ghost"
                              size="icon"
                              onClick={() => setDeleteId(funcionario.id)}
                              className="text-destructive hover:text-destructive"
                              title="Excluir"
                            >
                              <Trash2 className="h-4 w-4" />
                            </Button>
                          </div>
                        </TableCell>
                      </TableRow>
                    ))}
                  </TableBody>
                </Table>
              </div>
            )}
            
            {filteredFuncionarios.length > 0 && (
              <div className="mt-4 text-sm text-muted-foreground">
                Mostrando {filteredFuncionarios.length} de {funcionarios.length} funcionário(s)
              </div>
            )}
          </CardContent>
        </Card>
      </main>

      {/* Dialog de confirmação de exclusão */}
      <AlertDialog open={deleteId !== null} onOpenChange={() => setDeleteId(null)}>
        <AlertDialogContent>
          <AlertDialogHeader>
            <AlertDialogTitle>Confirmar exclusão</AlertDialogTitle>
            <AlertDialogDescription>
              Tem certeza que deseja excluir este funcionário? Esta ação não pode ser desfeita.
            </AlertDialogDescription>
          </AlertDialogHeader>
          <AlertDialogFooter>
            <AlertDialogCancel>Cancelar</AlertDialogCancel>
            <AlertDialogAction onClick={handleDelete} className="bg-destructive text-destructive-foreground hover:bg-destructive/90">
              Excluir
            </AlertDialogAction>
          </AlertDialogFooter>
        </AlertDialogContent>
      </AlertDialog>
    </div>
  )
}

