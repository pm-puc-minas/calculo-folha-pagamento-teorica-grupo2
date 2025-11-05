package br.com.folhapagamento.controller;

import br.com.folhapagamento.dto.LoginRequest;
import br.com.folhapagamento.dto.LoginResponse;
import br.com.folhapagamento.model.User;
import br.com.folhapagamento.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Autenticação", description = "Endpoints de autenticação de usuários")
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthService authService;
    
    @PostMapping("/login")
    @Operation(summary = "Fazer login", description = "Autentica um usuário no sistema")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request, HttpSession session) {
        logger.info("Login attempt: {}", request.getUsername());
        
        User user = authService.authenticate(request.getUsername(), request.getPassword());
        
        if (user != null) {
            session.setAttribute("user", user);
            session.setAttribute("authenticated", true);
            logger.info("Successful login: {}", user.getUsername());
            
            return ResponseEntity.ok(new LoginResponse(
                true,
                "Login realizado com sucesso",
                user.getUsername(),
                user.getNome(),
                user.getRole()
            ));
        }
        
        logger.warn("Failed login attempt: {}", request.getUsername());
        return ResponseEntity.status(401).body(
            new LoginResponse(false, "Usuário ou senha inválidos")
        );
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Fazer logout", description = "Encerra a sessão do usuário")
    public ResponseEntity<LoginResponse> logout(HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            logger.info("Logout do usuário: {}", user.getUsername());
        }
        
        session.invalidate();
        
        return ResponseEntity.ok(new LoginResponse(true, "Logout realizado com sucesso"));
    }
    
    @GetMapping("/me")
    @Operation(summary = "Obter usuário atual", description = "Retorna os dados do usuário autenticado")
    public ResponseEntity<LoginResponse> getCurrentUser(HttpSession session) {
        Boolean authenticated = (Boolean) session.getAttribute("authenticated");
        User user = (User) session.getAttribute("user");
        
        if (authenticated != null && authenticated && user != null) {
            LoginResponse response = new LoginResponse(
                true,
                "Usuário autenticado",
                user.getUsername(),
                user.getNome(),
                user.getRole()
            );
            return ResponseEntity.ok(response);
        }
        
        return ResponseEntity.status(401).body(
            new LoginResponse(false, "Usuário não autenticado")
        );
    }
}

