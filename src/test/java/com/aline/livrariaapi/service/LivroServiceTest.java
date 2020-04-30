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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    private Livro criarLivro() {
        return Livro.builder().id(1l).autor("aline").titulo("java").isbn("121").build();
    }
}
