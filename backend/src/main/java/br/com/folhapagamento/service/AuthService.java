package br.com.folhapagamento.service;

import br.com.folhapagamento.model.User;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    
    private final Map<String, User> users = new HashMap<>();
    
    public AuthService() {
        initializeDefaultUsers();
    }
    
    private void initializeDefaultUsers() {
        users.put("rh", new User("rh", "rh123", "Gestor RH", "USER"));
    }
    
    public User authenticate(String username, String password) {
        User user = users.get(username);
        
        if (user != null && user.getPassword().equals(password)) {
            return user;
        }
        
        return null;
    }
    
    public boolean userExists(String username) {
        return users.containsKey(username);
    }
}

