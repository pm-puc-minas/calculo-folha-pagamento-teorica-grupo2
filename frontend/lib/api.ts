const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

// ==================== AUTH ====================

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  success: boolean;
  message: string;
  username?: string;
  nome?: string;
  role?: string;
}

export async function login(credentials: LoginRequest): Promise<LoginResponse> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(credentials),
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(errorData.message || 'Erro ao fazer login');
    }

    return response.json();
  } catch (error) {
    if (error instanceof Error) {
      throw error;
    }
    throw new Error('Erro ao conectar com o servidor');
  }
}

export async function logout(): Promise<void> {
  try {
    await fetch(`${API_BASE_URL}/api/auth/logout`, {
      method: 'POST',
      credentials: 'include',
    });
  } catch (error) {
    console.error('Erro ao fazer logout:', error);
  }
}

export async function getCurrentUser(): Promise<LoginResponse | null> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/auth/me`, {
      method: 'GET',
      credentials: 'include',
    });

    if (!response.ok) {
      return null;
    }

    const data = await response.json();
    return data.success ? data : null;
  } catch (error) {
    return null;
  }
}

// ==================== INTERFACES ====================

export interface Funcionario {
  nome: string;
  cpf: string;
  cargo: string;
  departamento: string;
  tipo: string;
  salarioBruto: number;
  numeroDependentes: number;
  recebePericulosidade: boolean;
  grauInsalubridade: string;
  valorValeTransporte: number;
  valorValeAlimentacao: number;
}

export interface FuncionarioEntity extends Funcionario {
  id: number;
  dataCriacao: string;
  dataAtualizacao: string;
}

export interface FolhaPagamento {
  funcionario: Funcionario;
  salarioBruto: number;
  salarioPorHora: number;
  adicionalPericulosidade: number;
  adicionalInsalubridade: number;
  valeAlimentacao: number;
  descontoValeTransporte: number;
  descontoINSS: number;
  descontoIRRF: number;
  fgts: number;
  totalAntesDescontos: number;
  totalDescontos: number;
  salarioLiquido: number;
}

export interface FolhaPagamentoEntity {
  id: number;
  funcionario: FuncionarioEntity;
  mesReferencia: number;
  anoReferencia: number;
  salarioBruto: number;
  salarioPorHora: number;
  adicionalPericulosidade: number;
  adicionalInsalubridade: number;
  valeAlimentacao: number;
  descontoValeTransporte: number;
  descontoINSS: number;
  descontoIRRF: number;
  fgts: number;
  totalAntesDescontos: number;
  totalDescontos: number;
  salarioLiquido: number;
  dataCalculo: string;
}

export interface EstatisticasFuncionarios {
  totalFuncionarios: number;
  salarioMedio: number;
  salarioMaximo: number;
  salarioMinimo: number;
  totalDependentes: number;
  funcionariosComPericulosidade: number;
  funcionariosComInsalubridade: number;
}

export interface EstatisticasFolhas {
  totalFolhas: number;
  mediaSalarioLiquido: number;
  mediaSalarioBruto: number;
  totalDescontos: number;
  totalBeneficios: number;
}

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[];
}

// ==================== API ERROR ====================

export class APIError extends Error {
  status: number;
  error: string;
  details?: string[];
  
  constructor(errorResponse: ErrorResponse) {
    super(errorResponse.message);
    this.name = 'APIError';
    this.status = errorResponse.status;
    this.error = errorResponse.error;
    this.details = errorResponse.details;
  }
  
  isValidationError(): boolean {
    return this.status === 400 && this.error === 'Erro de Validação';
  }
  
  isSalarioError(): boolean {
    return this.error === 'Salário Inválido';
  }
  
  isDependentesError(): boolean {
    return this.error === 'Dependentes Inválidos';
  }
  
  isFuncionarioError(): boolean {
    return this.error === 'Funcionário Inválido';
  }
  
  isCalculoError(): boolean {
    return this.error === 'Erro no Cálculo';
  }
  
  isNotFoundError(): boolean {
    return this.status === 404;
  }
  
  isServerError(): boolean {
    return this.status >= 500;
  }
  
  getDetailedMessage(): string {
    if (this.details && this.details.length > 0) {
      return `${this.message}\n\nDetalhes:\n${this.details.map(d => `• ${d}`).join('\n')}`;
    }
    return this.message;
  }
}

// ==================== HELPER FUNCTIONS ====================

async function handleResponse<T>(response: Response): Promise<T> {
  if (!response.ok) {
    const errorData: ErrorResponse = await response.json().catch(() => ({
      timestamp: new Date().toISOString(),
      status: response.status,
      error: 'Erro',
      message: response.statusText || 'Erro desconhecido',
      path: '',
    }));
    throw new APIError(errorData);
  }
  return response.json();
}

function handleNetworkError(error: unknown): never {
  if (error instanceof APIError) {
    throw error;
  }
  
  if (error instanceof TypeError && error.message.includes('fetch')) {
    throw new Error('Não foi possível conectar ao servidor. Verifique se o backend está rodando.');
  }
  
  throw new Error('Erro inesperado. Tente novamente.');
}

// ==================== FUNCIONÁRIOS ====================

export async function listarFuncionarios(): Promise<FuncionarioEntity[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios`, {
      credentials: 'include',
    });
    return handleResponse<FuncionarioEntity[]>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function buscarFuncionario(id: number): Promise<FuncionarioEntity> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/${id}`, {
      credentials: 'include',
    });
    return handleResponse<FuncionarioEntity>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function criarFuncionario(funcionario: Funcionario): Promise<FuncionarioEntity> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(funcionario),
    });
    return handleResponse<FuncionarioEntity>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function atualizarFuncionario(id: number, funcionario: Funcionario): Promise<FuncionarioEntity> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/${id}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(funcionario),
    });
    return handleResponse<FuncionarioEntity>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function excluirFuncionario(id: number): Promise<void> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/${id}`, {
      method: 'DELETE',
      credentials: 'include',
    });
    
    if (!response.ok && response.status !== 204) {
      const errorData: ErrorResponse = await response.json().catch(() => ({
        timestamp: new Date().toISOString(),
        status: response.status,
        error: 'Erro',
        message: 'Erro ao excluir funcionário',
        path: '',
      }));
      throw new APIError(errorData);
    }
  } catch (error) {
    if (error instanceof APIError) throw error;
    handleNetworkError(error);
  }
}

export async function buscarFuncionarioPorCpf(cpf: string): Promise<FuncionarioEntity> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/cpf/${cpf}`, {
      credentials: 'include',
    });
    return handleResponse<FuncionarioEntity>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function filtrarFuncionariosPorTipo(tipo: string): Promise<FuncionarioEntity[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/tipo/${tipo}`, {
      credentials: 'include',
    });
    return handleResponse<FuncionarioEntity[]>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function obterEstatisticasFuncionarios(): Promise<EstatisticasFuncionarios> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/estatisticas`, {
      credentials: 'include',
    });
    return handleResponse<EstatisticasFuncionarios>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function agruparFuncionariosPorTipo(): Promise<Record<string, FuncionarioEntity[]>> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/funcionarios/agrupar/tipo`, {
      credentials: 'include',
    });
    return handleResponse<Record<string, FuncionarioEntity[]>>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

// ==================== FOLHAS DE PAGAMENTO ====================

export async function listarFolhas(): Promise<FolhaPagamentoEntity[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/folhas`, {
      credentials: 'include',
    });
    return handleResponse<FolhaPagamentoEntity[]>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function buscarFolha(id: number): Promise<FolhaPagamentoEntity> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/folhas/${id}`, {
      credentials: 'include',
    });
    return handleResponse<FolhaPagamentoEntity>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function listarFolhasPorFuncionario(funcionarioId: number): Promise<FolhaPagamentoEntity[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/folhas/funcionario/${funcionarioId}`, {
      credentials: 'include',
    });
    return handleResponse<FolhaPagamentoEntity[]>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function listarFolhasPorPeriodo(mes: number, ano: number): Promise<FolhaPagamentoEntity[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/folhas/mes/${mes}/ano/${ano}`, {
      credentials: 'include',
    });
    return handleResponse<FolhaPagamentoEntity[]>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function calcularESalvarFolha(funcionarioId: number): Promise<FolhaPagamentoEntity> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/folhas/calcular-e-salvar/${funcionarioId}`, {
      method: 'POST',
      credentials: 'include',
    });
    return handleResponse<FolhaPagamentoEntity>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function obterEstatisticasFolhas(mes?: number, ano?: number): Promise<EstatisticasFolhas> {
  try {
    let url = `${API_BASE_URL}/api/folhas/estatisticas`;
    const params = new URLSearchParams();
    if (mes) params.append('mes', mes.toString());
    if (ano) params.append('ano', ano.toString());
    if (params.toString()) url += `?${params.toString()}`;
    
    const response = await fetch(url, {
      credentials: 'include',
    });
    return handleResponse<EstatisticasFolhas>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

export async function ordenarFolhasPorSalarioLiquido(ascendente: boolean = true): Promise<FolhaPagamentoEntity[]> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/folhas/ordenar/salario-liquido?ascendente=${ascendente}`, {
      credentials: 'include',
    });
    return handleResponse<FolhaPagamentoEntity[]>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

// ==================== CÁLCULO DE FOLHA ====================

export async function calcularFolhaPagamento(funcionario: Funcionario): Promise<FolhaPagamento> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/calcular`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
      },
      credentials: 'include',
      body: JSON.stringify(funcionario),
    });
    return handleResponse<FolhaPagamento>(response);
  } catch (error) {
    return handleNetworkError(error);
  }
}

// ==================== TESTE DE API ====================

export async function testarAPI(): Promise<string> {
  try {
    const response = await fetch(`${API_BASE_URL}/api/teste`, {
      credentials: 'include'
    });
    
    if (!response.ok) {
      throw new Error('API não disponível');
    }

    return response.text();
  } catch (error) {
    if (error instanceof TypeError && error.message.includes('fetch')) {
      throw new Error('Backend offline. Inicie o servidor Spring Boot na porta 8080.');
    }
    throw error;
  }
}
