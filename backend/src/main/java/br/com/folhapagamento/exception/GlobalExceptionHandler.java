package br.com.folhapagamento.exception;

import br.com.folhapagamento.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {
    
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    
    /**
     * Trata erros de validação (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            MethodArgumentNotValidException ex, 
            HttpServletRequest request) {
        
        List<String> details = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            details.add(error.getField() + ": " + error.getDefaultMessage());
        }
        
        logger.warn("Erro de validação: {}", details);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Erro de Validação",
            "Os dados fornecidos são inválidos. Verifique os campos e tente novamente.",
            request.getRequestURI(),
            details
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções de salário inválido
     */
    @ExceptionHandler(SalarioInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleSalarioInvalidoException(
            SalarioInvalidoException ex,
            HttpServletRequest request) {
        
        logger.error("Salário inválido: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Salário Inválido",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções de dependentes inválidos
     */
    @ExceptionHandler(DependentesInvalidosException.class)
    public ResponseEntity<ErrorResponse> handleDependentesInvalidosException(
            DependentesInvalidosException ex,
            HttpServletRequest request) {
        
        logger.error("Dependentes inválidos: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Dependentes Inválidos",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções de funcionário inválido
     */
    @ExceptionHandler(FuncionarioInvalidoException.class)
    public ResponseEntity<ErrorResponse> handleFuncionarioInvalidoException(
            FuncionarioInvalidoException ex,
            HttpServletRequest request) {
        
        logger.error("Funcionário inválido: {}", ex.getMessage());
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Funcionário Inválido",
            ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções de cálculo
     */
    @ExceptionHandler(CalculoException.class)
    public ResponseEntity<ErrorResponse> handleCalculoException(
            CalculoException ex,
            HttpServletRequest request) {
        
        logger.error("Erro no cálculo: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro no Cálculo",
            "Ocorreu um erro ao calcular a folha de pagamento. " + ex.getMessage(),
            request.getRequestURI()
        );
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }
    
    /**
     * Trata erros de tipo de argumento incorreto
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatchException(
            MethodArgumentTypeMismatchException ex,
            HttpServletRequest request) {
        
        logger.warn("Tipo de argumento incorreto: {}", ex.getMessage());
        
        String message = String.format(
            "O valor '%s' não é válido para o campo '%s'. Esperado: %s",
            ex.getValue(),
            ex.getName(),
            ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "desconhecido"
        );
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Tipo de Dado Inválido",
            message,
            request.getRequestURI()
        );
        
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    /**
     * Trata exceções genéricas não previstas
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {
        
        logger.error("Erro não tratado: {}", ex.getMessage(), ex);
        
        ErrorResponse errorResponse = new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Erro Interno do Servidor",
            "Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.",
            request.getRequestURI()
        );
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}

