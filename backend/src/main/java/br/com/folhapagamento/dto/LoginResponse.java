package br.com.folhapagamento.dto;

public class LoginResponse {
    private boolean success;
    private String message;
    private String username;
    private String nome;
    private String role;
    
    public LoginResponse() {
    }
    
    public LoginResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    public LoginResponse(boolean success, String message, String username, String nome, String role) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.nome = nome;
        this.role = role;
    }
    
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getNome() {
        return nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
}


