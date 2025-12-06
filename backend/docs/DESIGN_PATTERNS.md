# Padrões de Projeto - Sistema de Folha de Pagamento

Este documento descreve os padrões de projeto implementados no backend do sistema de folha de pagamento.

## Índice

1. [Strategy Pattern](#strategy-pattern)
2. [Factory Pattern](#factory-pattern)
3. [Template Method Pattern](#template-method-pattern)
4. [Observer Pattern](#observer-pattern)
5. [Singleton Pattern](#singleton-pattern)

---

## Strategy Pattern

### Descrição

O padrão **Strategy** define uma família de algoritmos, encapsula cada um deles e os torna intercambiáveis. Este padrão permite que o algoritmo varie independentemente dos clientes que o utilizam.

### Implementação no Sistema

Utilizamos o Strategy Pattern para encapsular diferentes estratégias de cálculo da folha de pagamento:

```
┌─────────────────────────────────┐
│       CalculadoraFolha          │
│  (Context - Usa as Strategies)  │
└──────────────┬──────────────────┘
               │ usa
    ┌──────────┴──────────┐
    │                     │
    ▼                     ▼
┌───────────────┐   ┌───────────────┐
│ ICalculadora  │   │ ICalculadora  │
│   Salario     │   │   Adicionais  │
└───────┬───────┘   └───────┬───────┘
        │                   │
        ▼                   ▼
┌───────────────┐   ┌───────────────┐
│ Calculadora   │   │ Calculadora   │
│ SalarioImpl   │   │AdicionaisImpl │
└───────────────┘   └───────────────┘
```

### Interfaces (Strategies)

```java
// Estratégia para cálculo de salário
public interface ICalculadoraSalario {
    double calcularSalarioHora(double salarioBruto);
    double calcularSalarioDia(double salarioBruto);
}

// Estratégia para cálculo de adicionais
public interface ICalculadoraAdicionais {
    double calcularPericulosidade(Funcionario funcionario);
    double calcularInsalubridade(Funcionario funcionario);
}

// Estratégia para cálculo de benefícios
public interface ICalculadoraBeneficios {
    double calcularValeAlimentacao(Funcionario funcionario);
    double calcularDescontoValeTransporte(Funcionario funcionario);
}

// Estratégia para cálculo de descontos
public interface ICalculadoraDescontos {
    double calcularINSS(double baseCalculo);
    double calcularIRRF(double baseCalculo, int numeroDependentes);
    double calcularFGTS(double baseCalculo);
}
```

### Contexto (CalculadoraFolha)

```java
@Service
public class CalculadoraFolha implements IFolhaPagamentoService {
    
    @Autowired
    private ICalculadoraSalario calculadoraSalario;
    
    @Autowired
    private ICalculadoraAdicionais calculadoraAdicionais;
    
    @Autowired
    private ICalculadoraBeneficios calculadoraBeneficios;
    
    @Autowired
    private ICalculadoraDescontos calculadoraDescontos;
    
    @Override
    public FolhaPagamento calcularFolha(Funcionario funcionario) {
        // Usa as diferentes estratégias injetadas
        folha.setSalarioPorHora(calculadoraSalario.calcularSalarioHora(...));
        folha.setAdicionalPericulosidade(calculadoraAdicionais.calcularPericulosidade(...));
        // ... etc
    }
}
```

### Benefícios

- **Flexibilidade**: Fácil trocar ou adicionar novas estratégias de cálculo
- **Testabilidade**: Cada estratégia pode ser testada isoladamente
- **Manutenibilidade**: Mudanças em uma estratégia não afetam as outras
- **Extensibilidade**: Novas regras de cálculo (ex: novo imposto) podem ser adicionadas criando novas implementações

### Como Adicionar Nova Estratégia

Para adicionar uma nova forma de calcular IRRF progressivo, por exemplo:

```java
// 1. Criar nova implementação
@Component
@Primary  // ou use @Profile para ativar condicionalmente
public class CalculadoraDescontosProgressiva implements ICalculadoraDescontos {
    @Override
    public double calcularIRRF(double baseCalculo, int numeroDependentes) {
        // Nova lógica de cálculo progressivo
    }
}
```

---

## Factory Pattern

### Descrição

O padrão **Factory Method** define uma interface para criar objetos, mas permite que as subclasses decidam qual classe instanciar. O Factory Method permite que uma classe adie a instanciação para subclasses.

### Implementação no Sistema

Utilizamos o Factory Pattern para criar diferentes tipos de funcionários (CLT e PJ):

```
┌────────────────────────────────┐
│      FuncionarioFactory        │  Interface
│  + criarFuncionario(tipo, dto) │
└──────────────┬─────────────────┘
               │
               ▼
┌────────────────────────────────┐
│    FuncionarioFactoryImpl      │  Implementação
│  + criarFuncionario(tipo, dto) │
└──────────────┬─────────────────┘
               │
      ┌────────┴────────┐
      │                 │
      ▼                 ▼
┌───────────┐    ┌───────────┐
│FuncionarioCLT│ │FuncionarioPJ│
└───────────┘    └───────────┘
```

### Interface da Factory

```java
public interface FuncionarioFactory {
    FuncionarioBase criarFuncionario(String tipo, Funcionario funcionario);
}
```

### Implementação

```java
@Component
public class FuncionarioFactoryImpl implements FuncionarioFactory {
    
    @Override
    public FuncionarioBase criarFuncionario(String tipo, Funcionario funcionario) {
        return switch (tipo.toUpperCase().trim()) {
            case "CLT" -> criarFuncionarioCLT(funcionario);
            case "PJ" -> criarFuncionarioPJ(funcionario);
            default -> throw new IllegalArgumentException(
                "Tipo de funcionário inválido: '" + tipo + "'"
            );
        };
    }
    
    private FuncionarioCLT criarFuncionarioCLT(Funcionario funcionario) {
        FuncionarioCLT clt = new FuncionarioCLT();
        // Configura dados específicos de CLT
        clt.setNumeroDependentes(funcionario.getNumeroDependentes());
        clt.setRecebePericulosidade(funcionario.isRecebePericulosidade());
        // ...
        return clt;
    }
    
    private FuncionarioPJ criarFuncionarioPJ(Funcionario funcionario) {
        FuncionarioPJ pj = new FuncionarioPJ();
        // Configura dados específicos de PJ
        // ...
        return pj;
    }
}
```

### Uso

```java
@Service
public class FuncionarioService {
    
    @Autowired
    private FuncionarioFactory factory;
    
    public void processarFuncionario(String tipo, Funcionario dados) {
        FuncionarioBase funcionario = factory.criarFuncionario(tipo, dados);
        // Usa o funcionário criado
    }
}
```

### Benefícios

- **Encapsulamento**: Lógica de criação centralizada
- **Flexibilidade**: Fácil adicionar novos tipos (ex: Estagiário)
- **Desacoplamento**: Cliente não precisa conhecer classes concretas
- **Validação**: Validações de criação em um só lugar

---

## Template Method Pattern

### Descrição

O padrão **Template Method** define o esqueleto de um algoritmo em uma operação, postergando alguns passos para as subclasses. Permite que subclasses redefinam certos passos de um algoritmo sem mudar a estrutura do mesmo.

### Implementação no Sistema

Utilizamos na classe abstrata `CalculadoraBase`:

```java
public abstract class CalculadoraBase {
    
    // Template Method - define a estrutura do cálculo
    public final double calcularTotal(Funcionario funcionario) {
        double base = calcularBase(funcionario);
        double adicionais = calcularAdicionais(funcionario);
        double descontos = calcularDescontos(funcionario, base + adicionais);
        return base + adicionais - descontos;
    }
    
    // Métodos abstratos para subclasses implementarem
    protected abstract double calcularBase(Funcionario funcionario);
    protected abstract double calcularAdicionais(Funcionario funcionario);
    protected abstract double calcularDescontos(Funcionario funcionario, double baseCalculo);
}
```

---

## Observer Pattern

### Descrição

O padrão **Observer** define uma dependência um-para-muitos entre objetos, de modo que quando um objeto muda de estado, todos os seus dependentes são notificados.

### Implementação no Sistema

Utilizamos o sistema de eventos do Spring para implementar o padrão Observer:

```
┌──────────────────────┐
│  FuncionarioService  │ (Publisher)
│  + salvar()          │
└──────────┬───────────┘
           │ publica evento
           ▼
┌──────────────────────────────────┐
│  FuncionarioCadastradoEvent      │
└──────────────────────────────────┘
           │ notifica
    ┌──────┴──────┐
    │             │
    ▼             ▼
┌───────────┐ ┌────────────────┐
│LogListener│ │NotificacaoListener│
└───────────┘ └────────────────┘
```

### Eventos

```java
// Evento de funcionário cadastrado
public class FuncionarioCadastradoEvent extends ApplicationEvent {
    private final FuncionarioEntity funcionario;
    private final String mensagem;
    
    public FuncionarioCadastradoEvent(FuncionarioEntity source, String mensagem) {
        super(source);
        this.funcionario = source;
        this.mensagem = mensagem;
    }
}

// Evento de folha gerada
public class FolhaPagamentoGeradaEvent extends ApplicationEvent {
    private final FolhaPagamento folha;
    // ...
}
```

### Listeners (Observers)

```java
@Component
public class LogFuncionarioListener implements ApplicationListener<FuncionarioCadastradoEvent> {
    
    private static final Logger logger = LoggerFactory.getLogger(...);
    
    @Override
    public void onApplicationEvent(FuncionarioCadastradoEvent event) {
        logger.info("Novo funcionário cadastrado: {}", event.getFuncionario().getNome());
    }
}

@Component
public class NotificacaoFolhaListener implements ApplicationListener<FolhaPagamentoGeradaEvent> {
    
    @Override
    public void onApplicationEvent(FolhaPagamentoGeradaEvent event) {
        // Envia notificação, email, etc.
    }
}
```

### Publicação de Eventos

```java
@Service
public class FuncionarioService {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public FuncionarioEntity salvar(Funcionario funcionario) {
        FuncionarioEntity saved = repository.save(entity);
        
        // Publica evento para os observers
        eventPublisher.publishEvent(
            new FuncionarioCadastradoEvent(saved, "Funcionário cadastrado")
        );
        
        return saved;
    }
}
```

---

## Diagrama Geral

```
┌─────────────────────────────────────────────────────────────────┐
│                     Sistema de Folha de Pagamento                │
├─────────────────────────────────────────────────────────────────┤
│                                                                  │
│  ┌──────────────┐     ┌───────────────────┐                     │
│  │  Controller  │────▶│     Service       │                     │
│  └──────────────┘     └────────┬──────────┘                     │
│                                │                                 │
│           ┌────────────────────┼────────────────────┐           │
│           │                    │                    │           │
│           ▼                    ▼                    ▼           │
│  ┌─────────────────┐ ┌─────────────────┐ ┌─────────────────┐   │
│  │Factory Pattern  │ │Strategy Pattern │ │Observer Pattern │   │
│  │                 │ │                 │ │                 │   │
│  │FuncionarioFactory│ │ICalculadoraSalario│ │EventPublisher   │   │
│  │  ├─ CLT         │ │ICalculadoraAdic.│ │  ├─ LogListener │   │
│  │  └─ PJ          │ │ICalculadoraBenef│ │  └─ Notificacao│   │
│  │                 │ │ICalculadoraDesc.│ │                 │   │
│  └─────────────────┘ └─────────────────┘ └─────────────────┘   │
│                                                                  │
│  ┌─────────────────────────────────────────────────────────────┐│
│  │                   Template Method Pattern                    ││
│  │                                                              ││
│  │  CalculadoraBase (abstract)                                  ││
│  │    └─ calcularTotal() - define estrutura do algoritmo       ││
│  │        ├─ calcularBase() - implementado por subclasses      ││
│  │        ├─ calcularAdicionais() - implementado por subclasses││
│  │        └─ calcularDescontos() - implementado por subclasses ││
│  └─────────────────────────────────────────────────────────────┘│
│                                                                  │
└─────────────────────────────────────────────────────────────────┘
```

---

## Singleton Pattern

### Descrição

O padrão **Singleton** garante que uma classe tenha apenas uma instância e fornece um ponto de acesso global a ela.

### Implementação no Sistema

Utilizamos o Singleton para a classe `ConfiguracaoFiscal`, que armazena valores fiscais constantes:

```
┌─────────────────────────────────────────────────────────────────┐
│                    ConfiguracaoFiscal                            │
│                       (Singleton)                                │
├─────────────────────────────────────────────────────────────────┤
│ - instance: ConfiguracaoFiscal (static volatile)                │
│ - salarioMinimo: double                                         │
│ - horasMensais: int                                             │
│ - percentualFgts: double                                        │
│ - tabelaInss: Map<Double, Double>                               │
│ - tabelaIrrf: Map<Double, double[]>                             │
├─────────────────────────────────────────────────────────────────┤
│ - ConfiguracaoFiscal() (private)                                │
│ + getInstance(): ConfiguracaoFiscal (static)                    │
│ + getSalarioMinimo(): double                                    │
│ + getPercentualFgts(): double                                   │
│ + calcularInsalubridade(grau): double                           │
└─────────────────────────────────────────────────────────────────┘
```

### Código

```java
public class ConfiguracaoFiscal {
    
    private static volatile ConfiguracaoFiscal instance;
    
    private final double salarioMinimo = 1412.00;
    private final int horasMensais = 220;
    private final double percentualFgts = 0.08;
    
    // Construtor privado - impede instanciação externa
    private ConfiguracaoFiscal() {
        // Inicializa tabelas fiscais
    }
    
    // Double-checked locking para thread-safety
    public static ConfiguracaoFiscal getInstance() {
        if (instance == null) {
            synchronized (ConfiguracaoFiscal.class) {
                if (instance == null) {
                    instance = new ConfiguracaoFiscal();
                }
            }
        }
        return instance;
    }
    
    public double getSalarioMinimo() {
        return salarioMinimo;
    }
}
```

### Uso

```java
// Em qualquer lugar da aplicação
ConfiguracaoFiscal config = ConfiguracaoFiscal.getInstance();
double salarioMinimo = config.getSalarioMinimo();
double fgts = config.getPercentualFgts();
```

### Benefícios

- **Instância Única**: Garantia de uma única instância em toda a aplicação
- **Thread-Safe**: Implementação com double-checked locking
- **Acesso Global**: Ponto único de acesso aos valores fiscais
- **Imutabilidade**: Valores fiscais são final e não podem ser alterados

---

## Conclusão

A aplicação destes padrões de projeto traz diversos benefícios ao sistema:

| Padrão | Benefício Principal |
|--------|---------------------|
| **Strategy** | Flexibilidade para trocar algoritmos de cálculo |
| **Factory** | Encapsulamento da criação de objetos complexos |
| **Template Method** | Reutilização de código com pontos de extensão |
| **Observer** | Desacoplamento entre componentes do sistema |
| **Singleton** | Instância única para configurações globais |

Estes padrões trabalham em conjunto para criar um sistema:
- **Manutenível**: Fácil de modificar e estender
- **Testável**: Componentes podem ser testados isoladamente
- **Escalável**: Novas funcionalidades podem ser adicionadas sem afetar código existente
- **Desacoplado**: Mudanças em uma parte não propagam para outras

