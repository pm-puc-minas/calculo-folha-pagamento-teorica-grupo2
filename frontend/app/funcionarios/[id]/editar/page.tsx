"use client"

import { useState, useEffect } from 'react'
import { useRouter, useParams } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { FuncionarioForm } from '@/components/funcionario-form'
import { buscarFuncionario, atualizarFuncionario, type Funcionario, type FuncionarioEntity } from '@/lib/api'
import { toast } from 'sonner'
import { Loader2 } from 'lucide-react'

export default function EditarFuncionarioPage() {
  const { isAuthenticated, loading: authLoading } = useAuth()
  const router = useRouter()
  const params = useParams()
  const id = Number(params.id)
  
  const [funcionario, setFuncionario] = useState<FuncionarioEntity | null>(null)
  const [loading, setLoading] = useState(true)
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login')
      return
    }
    
    if (isAuthenticated && id) {
      carregarFuncionario()
    }
  }, [isAuthenticated, authLoading, id])

  const carregarFuncionario = async () => {
    try {
      const data = await buscarFuncionario(id)
      setFuncionario(data)
    } catch (error) {
      toast.error('Funcionário não encontrado')
      router.push('/funcionarios')
    } finally {
      setLoading(false)
    }
  }

  const handleSubmit = async (data: Funcionario) => {
    setSaving(true)
    try {
      await atualizarFuncionario(id, data)
      toast.success('Funcionário atualizado com sucesso!')
      router.push('/funcionarios')
    } catch (error) {
      toast.error('Erro ao atualizar funcionário')
      console.error(error)
    } finally {
      setSaving(false)
    }
  }

  if (authLoading || loading) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <Loader2 className="h-12 w-12 animate-spin text-primary" />
      </div>
    )
  }

  if (!isAuthenticated || !funcionario) {
    return null
  }

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      <main className="container mx-auto px-4 py-6 max-w-3xl">
        <FuncionarioForm 
          initialData={funcionario}
          onSubmit={handleSubmit}
          onCancel={() => router.push('/funcionarios')}
          loading={saving}
          title="Editar Funcionário"
          description={`Atualize os dados de ${funcionario.nome}`}
        />
      </main>
    </div>
  )
}

