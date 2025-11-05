const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

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

export interface Funcionario {
  nome: string;
  cpf: string;
  cargo: string;
  tipo: string;
  salarioBruto: number;
  numeroDependentes: number;
  recebePericulosidade: boolean;
  grauInsalubridade: string;
  valorValeTransporte: number;
  valorValeAlimentacao: number;
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

export interface ErrorResponse {
  timestamp: string;
  status: number;
  error: string;
  message: string;
  path: string;
  details?: string[];
}

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

    if (!response.ok) {
      const errorData: ErrorResponse = await response.json();
      throw new APIError(errorData);
    }

    return response.json();
  } catch (error) {
    if (error instanceof APIError) {
      throw error;
    }
    
    // Erro de rede ou outro erro não esperado
    if (error instanceof TypeError && error.message.includes('fetch')) {
      throw new Error('Não foi possível conectar ao servidor. Verifique se o backend está rodando na porta 8080.');
    }
    
    throw new Error('Erro inesperado ao calcular folha de pagamento. Tente novamente.');
  }
}

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


