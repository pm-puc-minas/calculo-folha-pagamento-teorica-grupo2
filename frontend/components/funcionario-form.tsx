"use client"

import { useState, useEffect } from 'react'
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card'
import { Input } from '@/components/ui/input'
import { Label } from '@/components/ui/label'
import { Button } from '@/components/ui/button'
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/ui/select'
import { Checkbox } from '@/components/ui/checkbox'
import { Loader2, UserPlus, ArrowLeft } from 'lucide-react'
import { type Funcionario, type FuncionarioEntity } from '@/lib/api'

interface FuncionarioFormProps {
  initialData?: FuncionarioEntity
  onSubmit: (data: Funcionario) => Promise<void>
  onCancel: () => void
  loading?: boolean
  title: string
  description: string
}

interface FormErrors {
  nome?: string
  cpf?: string
  cargo?: string
  tipo?: string
  salarioBruto?: string
  numeroDependentes?: string
  valorValeTransporte?: string
  valorValeAlimentacao?: string
}

export function FuncionarioForm({
  initialData,
  onSubmit,
  onCancel,
  loading = false,
  title,
  description
}: FuncionarioFormProps) {
  const [formData, setFormData] = useState<Funcionario>({
    nome: '',
    cpf: '',
    cargo: '',
    departamento: '',
    tipo: 'CLT',
    salarioBruto: 0,
    numeroDependentes: 0,
    recebePericulosidade: false,
    grauInsalubridade: '',
    valorValeTransporte: 0,
    valorValeAlimentacao: 0,
  })
  
  const [errors, setErrors] = useState<FormErrors>({})

  useEffect(() => {
    if (initialData) {
      setFormData({
        nome: initialData.nome,
        cpf: initialData.cpf || '',
        cargo: initialData.cargo,
        departamento: initialData.departamento || '',
        tipo: initialData.tipo,
        salarioBruto: initialData.salarioBruto,
        numeroDependentes: initialData.numeroDependentes,
        recebePericulosidade: initialData.recebePericulosidade,
        grauInsalubridade: initialData.grauInsalubridade || '',
        valorValeTransporte: initialData.valorValeTransporte,
        valorValeAlimentacao: initialData.valorValeAlimentacao,
      })
    }
  }, [initialData])

  const isCLT = formData.tipo === 'CLT'

  const validate = (): boolean => {
    const newErrors: FormErrors = {}
    
    if (!formData.nome || formData.nome.trim().length < 3) {
      newErrors.nome = 'Nome deve ter no mínimo 3 caracteres'
    }
    
    if (!formData.cargo || formData.cargo.trim().length < 2) {
      newErrors.cargo = 'Cargo deve ter no mínimo 2 caracteres'
    }
    
    if (!formData.tipo) {
      newErrors.tipo = 'Tipo é obrigatório'
    }
    
    if (!formData.salarioBruto || formData.salarioBruto < 1320) {
      newErrors.salarioBruto = 'Salário deve ser no mínimo R$ 1.320,00'
    }
    
    if (formData.salarioBruto > 100000) {
      newErrors.salarioBruto = 'Salário não pode exceder R$ 100.000,00'
    }
    
    if (isCLT && (formData.numeroDependentes < 0 || formData.numeroDependentes > 20)) {
      newErrors.numeroDependentes = 'Dependentes deve ser entre 0 e 20'
    }
    
    if (formData.valorValeTransporte < 0) {
      newErrors.valorValeTransporte = 'Valor não pode ser negativo'
    }
    
    if (formData.valorValeAlimentacao < 0) {
      newErrors.valorValeAlimentacao = 'Valor não pode ser negativo'
    }
    
    setErrors(newErrors)
    return Object.keys(newErrors).length === 0
  }

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault()
    
    if (!validate()) return
    
    // Se for PJ, limpar campos específicos de CLT
    const submitData = { ...formData }
    if (!isCLT) {
      submitData.numeroDependentes = 0
      submitData.recebePericulosidade = false
      submitData.grauInsalubridade = ''
      submitData.valorValeTransporte = 0
    }
    
    await onSubmit(submitData)
  }

  const updateField = <K extends keyof Funcionario>(field: K, value: Funcionario[K]) => {
    setFormData(prev => ({ ...prev, [field]: value }))
    if (errors[field as keyof FormErrors]) {
      setErrors(prev => ({ ...prev, [field]: undefined }))
    }
  }

  return (
    <Card>
      <CardHeader>
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="icon" onClick={onCancel}>
            <ArrowLeft className="h-4 w-4" />
          </Button>
          <div>
            <CardTitle className="flex items-center gap-2">
              <UserPlus className="h-5 w-5" />
              {title}
            </CardTitle>
            <CardDescription>{description}</CardDescription>
          </div>
        </div>
      </CardHeader>
      
      <CardContent>
        <form onSubmit={handleSubmit} className="space-y-6">
          {/* Dados Pessoais */}
          <div className="space-y-4">
            <h3 className="font-medium text-sm text-muted-foreground uppercase tracking-wide">
              Dados Pessoais
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="nome">Nome *</Label>
                <Input
                  id="nome"
                  value={formData.nome}
                  onChange={(e) => updateField('nome', e.target.value)}
                  placeholder="Nome completo"
                  className={errors.nome ? 'border-destructive' : ''}
                />
                {errors.nome && <p className="text-sm text-destructive">{errors.nome}</p>}
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="cpf">CPF</Label>
                <Input
                  id="cpf"
                  value={formData.cpf}
                  onChange={(e) => updateField('cpf', e.target.value)}
                  placeholder="000.000.000-00"
                />
              </div>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="cargo">Cargo *</Label>
                <Input
                  id="cargo"
                  value={formData.cargo}
                  onChange={(e) => updateField('cargo', e.target.value)}
                  placeholder="Ex: Desenvolvedor"
                  className={errors.cargo ? 'border-destructive' : ''}
                />
                {errors.cargo && <p className="text-sm text-destructive">{errors.cargo}</p>}
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="departamento">Departamento</Label>
                <Input
                  id="departamento"
                  value={formData.departamento}
                  onChange={(e) => updateField('departamento', e.target.value)}
                  placeholder="Ex: Tecnologia"
                />
              </div>
            </div>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="tipo">Tipo de Contrato *</Label>
                <Select 
                  value={formData.tipo} 
                  onValueChange={(value) => updateField('tipo', value)}
                >
                  <SelectTrigger className={errors.tipo ? 'border-destructive' : ''}>
                    <SelectValue placeholder="Selecione o tipo" />
                  </SelectTrigger>
                  <SelectContent>
                    <SelectItem value="CLT">CLT</SelectItem>
                    <SelectItem value="PJ">PJ (Pessoa Jurídica)</SelectItem>
                  </SelectContent>
                </Select>
                {errors.tipo && <p className="text-sm text-destructive">{errors.tipo}</p>}
              </div>
            </div>
          </div>

          {/* Dados Financeiros */}
          <div className="space-y-4">
            <h3 className="font-medium text-sm text-muted-foreground uppercase tracking-wide">
              Dados Financeiros
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="salarioBruto">Salário Bruto (R$) *</Label>
                <Input
                  id="salarioBruto"
                  type="number"
                  step="0.01"
                  min="1320"
                  max="100000"
                  value={formData.salarioBruto || ''}
                  onChange={(e) => updateField('salarioBruto', parseFloat(e.target.value) || 0)}
                  placeholder="5000.00"
                  className={`font-mono ${errors.salarioBruto ? 'border-destructive' : ''}`}
                />
                {errors.salarioBruto && <p className="text-sm text-destructive">{errors.salarioBruto}</p>}
              </div>
              
              {isCLT && (
                <div className="space-y-2">
                  <Label htmlFor="numeroDependentes">Número de Dependentes</Label>
                  <Input
                    id="numeroDependentes"
                    type="number"
                    min="0"
                    max="20"
                    value={formData.numeroDependentes || ''}
                    onChange={(e) => updateField('numeroDependentes', parseInt(e.target.value) || 0)}
                    placeholder="0"
                    className={errors.numeroDependentes ? 'border-destructive' : ''}
                  />
                  {errors.numeroDependentes && <p className="text-sm text-destructive">{errors.numeroDependentes}</p>}
                </div>
              )}
            </div>
          </div>

          {/* Adicionais (apenas CLT) */}
          {isCLT && (
            <div className="space-y-4">
              <h3 className="font-medium text-sm text-muted-foreground uppercase tracking-wide">
                Adicionais
              </h3>
              
              <div className="space-y-4">
                <div className="flex items-center space-x-2">
                  <Checkbox
                    id="periculosidade"
                    checked={formData.recebePericulosidade}
                    onCheckedChange={(checked) => updateField('recebePericulosidade', !!checked)}
                  />
                  <Label htmlFor="periculosidade" className="cursor-pointer">
                    Recebe adicional de periculosidade (30%)
                  </Label>
                </div>
                
                <div className="space-y-2">
                  <Label htmlFor="insalubridade">Grau de Insalubridade</Label>
                  <Select 
                    value={formData.grauInsalubridade || "NENHUM"} 
                    onValueChange={(value) => updateField('grauInsalubridade', value === "NENHUM" ? "" : value)}
                  >
                    <SelectTrigger>
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
            </div>
          )}

          {/* Benefícios */}
          <div className="space-y-4">
            <h3 className="font-medium text-sm text-muted-foreground uppercase tracking-wide">
              Benefícios
            </h3>
            
            <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
              {isCLT && (
                <div className="space-y-2">
                  <Label htmlFor="valeTransporte">Vale Transporte Mensal (R$)</Label>
                  <Input
                    id="valeTransporte"
                    type="number"
                    step="0.01"
                    min="0"
                    value={formData.valorValeTransporte || ''}
                    onChange={(e) => updateField('valorValeTransporte', parseFloat(e.target.value) || 0)}
                    placeholder="200.00"
                    className={`font-mono ${errors.valorValeTransporte ? 'border-destructive' : ''}`}
                  />
                  {errors.valorValeTransporte && <p className="text-sm text-destructive">{errors.valorValeTransporte}</p>}
                </div>
              )}
              
              <div className="space-y-2">
                <Label htmlFor="valeAlimentacao">Vale Alimentação Mensal (R$)</Label>
                <Input
                  id="valeAlimentacao"
                  type="number"
                  step="0.01"
                  min="0"
                  value={formData.valorValeAlimentacao || ''}
                  onChange={(e) => updateField('valorValeAlimentacao', parseFloat(e.target.value) || 0)}
                  placeholder="1200.00"
                  className={`font-mono ${errors.valorValeAlimentacao ? 'border-destructive' : ''}`}
                />
                {errors.valorValeAlimentacao && <p className="text-sm text-destructive">{errors.valorValeAlimentacao}</p>}
              </div>
            </div>
          </div>

          {/* Botões */}
          <div className="flex justify-end gap-4 pt-4 border-t">
            <Button type="button" variant="outline" onClick={onCancel}>
              Cancelar
            </Button>
            <Button type="submit" disabled={loading}>
              {loading ? (
                <>
                  <Loader2 className="mr-2 h-4 w-4 animate-spin" />
                  Salvando...
                </>
              ) : (
                'Salvar'
              )}
            </Button>
          </div>
        </form>
      </CardContent>
    </Card>
  )
}

