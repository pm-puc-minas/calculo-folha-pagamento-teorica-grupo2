"use client"

import React, { createContext, useContext, useState, useEffect } from 'react'
import { useRouter } from 'next/navigation'
import { login as apiLogin, logout as apiLogout, getCurrentUser, type LoginRequest, type LoginResponse } from '@/lib/api'
import { toast } from 'sonner'

interface AuthContextType {
  user: LoginResponse | null
  loading: boolean
  login: (credentials: LoginRequest) => Promise<void>
  logout: () => Promise<void>
  isAuthenticated: boolean
}

const AuthContext = createContext<AuthContextType | undefined>(undefined)

export function AuthProvider({ children }: { children: React.Node }) {
  const [user, setUser] = useState<LoginResponse | null>(null)
  const [loading, setLoading] = useState(true)
  const router = useRouter()

  useEffect(() => {
    checkAuth()
  }, [])

  const checkAuth = async () => {
    try {
      const currentUser = await getCurrentUser()
      if (currentUser) {
        setUser(currentUser)
      }
    } catch (error) {
      console.error('Erro ao verificar autenticação:', error)
    } finally {
      setLoading(false)
    }
  }

  const login = async (credentials: LoginRequest) => {
    try {
      const response = await apiLogin(credentials)
      setUser(response)
      toast.success(`Bem-vindo, ${response.nome}!`)
      router.push('/dashboard')
    } catch (error) {
      if (error instanceof Error) {
        toast.error(error.message)
      } else {
        toast.error('Erro ao fazer login')
      }
      throw error
    }
  }

  const logout = async () => {
    try {
      await apiLogout()
      setUser(null)
      toast.success('Logout realizado com sucesso')
      router.push('/login')
    } catch (error) {
      console.error('Erro ao fazer logout:', error)
    }
  }

  const value = {
    user,
    loading,
    login,
    logout,
    isAuthenticated: !!user,
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
  const context = useContext(AuthContext)
  if (context === undefined) {
    throw new Error('useAuth deve ser usado dentro de um AuthProvider')
  }
  return context
}

