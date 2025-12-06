"use client"

import { useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { useAuth } from '@/contexts/AuthContext'
import { Header } from '@/components/header'
import { FuncionarioForm } from '@/components/funcionario-form'
import { criarFuncionario, type Funcionario } from '@/lib/api'
import { toast } from 'sonner'
import { Loader2 } from 'lucide-react'

export default function NovoFuncionarioPage() {
  const { isAuthenticated, loading: authLoading } = useAuth()
  const router = useRouter()
  const [saving, setSaving] = useState(false)

  useEffect(() => {
    if (!authLoading && !isAuthenticated) {
      router.push('/login')
    }
  }, [authLoading, isAuthenticated, router])

  const handleSubmit = async (data: Funcionario) => {
    setSaving(true)
    try {
      await criarFuncionario(data)
      toast.success('Funcionário cadastrado com sucesso!')
      router.push('/funcionarios')
    } catch (error) {
      toast.error('Erro ao cadastrar funcionário')
      console.error(error)
    } finally {
      setSaving(false)
    }
  }

  if (authLoading || !isAuthenticated) {
    return (
      <div className="min-h-screen flex items-center justify-center bg-background">
        <Loader2 className="h-12 w-12 animate-spin text-primary" />
      </div>
    )
  }

  return (
    <div className="min-h-screen bg-background">
      <Header />
      
      <main className="container mx-auto px-4 py-6 max-w-3xl">
        <FuncionarioForm 
          onSubmit={handleSubmit}
          onCancel={() => router.push('/funcionarios')}
          loading={saving}
          title="Novo Funcionário"
          description="Preencha os dados para cadastrar um novo funcionário"
        />
      </main>
    </div>
  )
}

