package com.easyevents.auth_service.domain.Exception;

import com.easyevents.auth_service.domain.dto.response.ErrorResponse;
import org.slf4j.Logger; // Importar Logger
import org.slf4j.LoggerFactory; // Importar LoggerFactory
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException; // Importar NoResourceFoundException

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Manipulador para NoResourceFoundException.
     * Retorna HTTP 404 quando um recurso (ex: endpoint ou arquivo estático) não é encontrado.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException ex) {
        String mensagem = "O recurso solicitado na URI '" + ex.getResourcePath() + "' não foi encontrado.";
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.NOT_FOUND.value())
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(mensagem)
                .build();

        // Logar como aviso, pois não é um erro crítico do sistema, mas uma requisição inválida.
        logger.warn("Recurso não encontrado ({}): {}", HttpStatus.NOT_FOUND.value(), mensagem, ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
    }

    /**
     * Manipulador para RuntimeException.
     * Retorna HTTP 400 por padrão, mas pode ser ajustado.
     * É uma boa prática logar estas exceções.
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeException(RuntimeException ex) {
        // Logar como aviso ou erro, dependendo da severidade esperada para RuntimeExceptions genéricas
        logger.warn("Uma RuntimeException ocorreu: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value()) // Considere se BAD_REQUEST é sempre apropriado.
                // Algumas RuntimeExceptions podem justificar um 500.
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage() != null ? ex.getMessage() : "Ocorreu um erro durante o processamento da sua requisição.")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }

    /**
     * Manipulador genérico para qualquer outra Exception não tratada.
     * Retorna HTTP 500 Internal Server Error.
     * É CRUCIAL logar a stack trace completa para diagnóstico.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Logar como ERRO com a stack trace completa.
        logger.error("Ocorreu um erro inesperado no servidor (Exceção Genérica):", ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message("Ocorreu um erro inesperado. Por favor, tente novamente mais tarde.") // Mensagem mais amigável para o usuário
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}