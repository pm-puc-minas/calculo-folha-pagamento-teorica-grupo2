package br.com.folhapagamento.enums;

public enum GrauInsalubridade {
    BAIXO("baixo", 0.10),
    MEDIO("medio", 0.20),
    ALTO("alto", 0.40);
    
    private final String valor;
    private final double percentual;
    
    GrauInsalubridade(String valor, double percentual) {
        this.valor = valor;
        this.percentual = percentual;
    }
    
    public String getValor() {
        return valor;
    }
    
    public double getPercentual() {
        return percentual;
    }
    
    public static GrauInsalubridade fromString(String valor) {
        for (GrauInsalubridade grau : values()) {
            if (grau.valor.equals(valor)) {
                return grau;
            }
        }
        return null;
    }
}
