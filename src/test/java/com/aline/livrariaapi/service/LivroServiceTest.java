package com.aline.livrariaapi.service;

import com.aline.livrariaapi.exception.BusinessException;
import com.aline.livrariaapi.model.entity.Livro;
import com.aline.livrariaapi.model.repository.LivroRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.internal.verification.Times;
import org.mockito.verification.VerificationMode;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Optional;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LivroServiceTest {

    LivroService service;
    @MockBean
    LivroRepository repository;

    @BeforeEach
    public void setup() {
        this.service = new LivroServiceImpl(repository);
    }

    @Test
    @DisplayName("Deve salvar livro")
    public void deveSalvarLivro() {
        //cenario
        Livro livro = criarLivro();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(false);
        Mockito.when(repository.save(livro))
                .thenReturn(criarLivro());
        //execucao
        Livro livroSalvo = service.save(livro);

        //verificacao
        assertThat(livroSalvo.getId()).isNotNull();
        assertThat(livroSalvo.getIsbn()).isEqualTo("121");
        assertThat(livroSalvo.getTitulo()).isEqualTo("java");
        assertThat(livroSalvo.getAutor()).isEqualTo("aline");

    }


    @Test
    @DisplayName("Deve lançar businessException quando já existir o isbn cadastrado")
    public void naoDeveSalvarLivroComIsbnDuplicado() {
        //cenario
        Livro livro = criarLivro();
        Mockito.when(repository.existsByIsbn(Mockito.anyString())).thenReturn(true);

        //execucao
        Throwable throwable = Assertions.catchThrowable(() -> service.save(livro));

        //verificacao
        Assertions.assertThat(throwable).isInstanceOf(BusinessException.class)
                .hasMessage("Isbn já cadastrado.");
        Mockito.verify(repository, Mockito.never()).save(livro);

    }

    @Test
    @DisplayName("Deve retornar um livro pelo id informado")
    public void findById() {
        //cenario
        Long id = 1L;
        Livro livro = criarLivro();
        livro.setId(id);

        Mockito.when(repository.findById(id)).thenReturn(Optional.of(livro));

        //execucao
        Optional<Livro> livroEncontrado = service.findById(id);

        //verificaçao
        assertThat(livroEncontrado.isPresent()).isTrue();
        assertThat(livroEncontrado.get().getId()).isEqualTo(id);
        assertThat(livroEncontrado.get().getIsbn()).isEqualTo(livro.getIsbn());
        assertThat(livroEncontrado.get().getTitulo()).isEqualTo(livro.getTitulo());
        assertThat(livroEncontrado.get().getAutor()).isEqualTo(livro.getAutor());
    }


    @Test
    @DisplayName("Deve retornar empty quando um livro pelo id informado não existir")
    public void livroNaoEncontradofindById() {
        //cenario
        Long id = 1L;

        Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

        //execucao
        Optional<Livro> livro = service.findById(id);

        //verificaçao
        assertThat(livro.isPresent()).isFalse();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deveDeletarUmLivro() {
        //cenario
        Long id = 1L;
        Livro livro = criarLivro();
        livro.setId(id);

        //execucao
        org.junit.jupiter.api.Assertions.assertDoesNotThrow(() -> service.deletarLivro(livro));

        Mockito.verify(repository, Mockito.times(1)).delete(livro);
    }

    @Test
    @DisplayName("Deve retornar exceção ao deletar um livro nulo")
    public void deveRetornarExcecaoDeletarUmLivroNulo() {
        //execucao
        Livro livro = criarLivro();
        livro.setId(null);

        //verificacao
        assertThrows(IllegalArgumentException.class, () -> service.deletarLivro(livro));
        Mockito.verify(repository, Mockito.never()).delete(livro);
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void deveAtualizarUmLivro() {
        //cenario
        Long id = 1L;
        Livro livroParaAtualizar = Livro.builder().id(id).build();
        Livro livroAtualizado = criarLivro();
        livroAtualizado.setId(id);
        Mockito.when(repository.save(livroParaAtualizar)).thenReturn(livroAtualizado);

        //execucao
        Livro livro = service.atualizarLivro(livroParaAtualizar);

        //verificaçao
        assertThat(livro.getId()).isEqualTo(id);
        assertThat(livro.getIsbn()).isEqualTo(livroAtualizado.getIsbn());
        assertThat(livro.getTitulo()).isEqualTo(livroAtualizado.getTitulo());
        assertThat(livro.getAutor()).isEqualTo(livroAtualizado.getAutor());
    }

    @Test
    @DisplayName("Deve retornar exceção ao atualizar um livro inexistente")
    public void deveRetornarExcecaoAtualizarUmLivroInexistente() {
        //execucao
        Livro livro = criarLivro();
        livro.setId(null);

        //verificacao
        assertThrows(IllegalArgumentException.class, () -> service.atualizarLivro(livro));
        Mockito.verify(repository, Mockito.never()).save(livro);
    }

    private Livro criarLivro() {
        return Livro.builder().id(1l).autor("aline").titulo("java").isbn("121").build();
    }

    @Test
    public void teste() throws ParseException {

        String date_s = " 2011-01-18 00:00:00.0";
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        Date date = dt.parse(date_s);
        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-mm-dd");
        System.out.println(dt1.format(date));


    }
}
