package br.com.fiap3ess.autoescola3ess.infra.exception;

import br.com.fiap3ess.autoescola3ess.domain.agenda.ValidacaoException;
import br.com.fiap3ess.autoescola3ess.domain.aluno.AlunoNotFoundException;
import br.com.fiap3ess.autoescola3ess.domain.instrutor.InstrutorNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DadosErro> tratarGenericNotFound() {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new DadosErro(
                        "Registro não encontrado!"
                ));
    }

    @ExceptionHandler({
            InstrutorNotFoundException.class,
            AlunoNotFoundException.class
    })
    public ResponseEntity<DadosErro> tratarNotFound(
            RuntimeException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new DadosErro(
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(ValidacaoException.class)
    public ResponseEntity<DadosErro> tratarRegraDeNegocio(
            ValidacaoException exception
    ) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new DadosErro(
                        exception.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<List<DadosBadRequest>>
    tratarBadRequest(
            MethodArgumentNotValidException exception
    ) {
        List<DadosBadRequest> erros = exception
                .getFieldErrors()
                .stream()
                .map(DadosBadRequest::new)
                .toList();

        return ResponseEntity
                .badRequest()
                .body(erros);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<DadosErro> tratarJsonInvalido() {
        return ResponseEntity
                .badRequest()
                .body(new DadosErro(
                        "JSON inválido ou valor não permitido!"
                ));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<DadosErro> tratarLoginInvalido() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new DadosErro(
                        "Login ou senha inválidos!"
                ));
    }

    @ExceptionHandler(br.com.fiap3ess.autoescola3ess.service.CepNaoEncontradoException.class)
    public ResponseEntity<DadosErro> tratarCepNaoEncontrado(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new DadosErro(exception.getMessage()));
    }

    @ExceptionHandler(br.com.fiap3ess.autoescola3ess.service.ServicoExternoException.class)
    public ResponseEntity<DadosErro> tratarServicoExterno(RuntimeException exception) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(new DadosErro(exception.getMessage()));
    }

    private record DadosBadRequest(
            String campo,
            String mensagem
    ) {
        public DadosBadRequest(FieldError erro) {
            this(
                    erro.getField(),
                    erro.getDefaultMessage()
            );
        }
    }

    private record DadosErro(String mensagem) {
    }
}