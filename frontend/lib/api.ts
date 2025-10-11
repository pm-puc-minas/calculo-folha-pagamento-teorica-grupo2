const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';

export interface Funcionario {
  nome: string;
  cpf: string;
  cargo: string;
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

export async function calcularFolhaPagamento(funcionario: Funcionario): Promise<FolhaPagamento> {
  const response = await fetch(`${API_BASE_URL}/api/calcular`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(funcionario),
  });

  if (!response.ok) {
    throw new Error('Erro ao calcular folha de pagamento');
  }

  return response.json();
}

export async function testarAPI(): Promise<string> {
  const response = await fetch(`${API_BASE_URL}/api/teste`);
  
  if (!response.ok) {
    throw new Error('API não disponível');
  }

  return response.text();
}


