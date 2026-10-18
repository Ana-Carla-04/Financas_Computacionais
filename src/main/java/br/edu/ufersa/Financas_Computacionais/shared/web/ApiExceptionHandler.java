package br.edu.ufersa.Financas_Computacionais.shared.web;

import br.edu.ufersa.Financas_Computacionais.shared.exception.ParametroInvalidoException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> tratarValidacaoDeRequest(
            MethodArgumentNotValidException exception) {
        Map<String, String> erros = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(erro ->
                erros.putIfAbsent(erro.getField(), erro.getDefaultMessage()));
        exception.getBindingResult().getGlobalErrors().forEach(erro ->
                erros.putIfAbsent("request", erro.getDefaultMessage()));

        return criarResposta(HttpStatus.BAD_REQUEST,
                "A requisicao contem campos invalidos.", erros);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> tratarViolacaoDeConstraint(
            ConstraintViolationException exception) {
        Map<String, String> erros = new LinkedHashMap<>();
        exception.getConstraintViolations().forEach(violacao ->
                erros.put(violacao.getPropertyPath().toString(), violacao.getMessage()));
        return criarResposta(HttpStatus.BAD_REQUEST,
                "A requisicao contem campos invalidos.", erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> tratarJsonInvalido(HttpMessageNotReadableException exception) {
        return criarResposta(HttpStatus.BAD_REQUEST,
                "O corpo da requisicao esta ausente ou possui JSON invalido.", Map.of());
    }

    @ExceptionHandler(ParametroInvalidoException.class)
    public ResponseEntity<ApiError> tratarParametroInvalido(ParametroInvalidoException exception) {
        return criarResposta(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> tratarArgumentoInvalido(IllegalArgumentException exception) {
        return criarResposta(HttpStatus.BAD_REQUEST, exception.getMessage(), Map.of());
    }

    private ResponseEntity<ApiError> criarResposta(HttpStatus status, String mensagem,
                                                   Map<String, String> erros) {
        return ResponseEntity.status(status).body(new ApiError(
                Instant.now(), status.value(), status.getReasonPhrase(), mensagem, erros));
    }
}