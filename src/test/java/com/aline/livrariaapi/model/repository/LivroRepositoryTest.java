package com.aline.livrariaapi.model.repository;

import com.aline.livrariaapi.model.entity.Livro;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import sun.misc.LRUCache;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class LivroRepositoryTest {


    /**
     * TestEntityManager provides a subset of EntityManager methods that are useful for tests
     * as well as helper methods for common testing tasks such as persist or find.
     */
    @Autowired
    TestEntityManager entityManager;

    @Autowired
    LivroRepository repository;

    @Test
    @DisplayName("Deve retornar true quando o isbn já existir na base")
    public void deveRetornarTrueQuandoIsbnJaExistir() {
        //cenario
        String isbn = "121";
        entityManager.persist(criarLivro());
        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isTrue();

    }

    @Test
    @DisplayName("Deve retornar false quando o isbn não existir na base")
    public void deveRetornarFalseQuandoIsbnNaoExistir() {
        //cenario
        String isbn = "121";
        //execucao
        boolean exists = repository.existsByIsbn(isbn);

        //verificacao
        Assertions.assertThat(exists).isFalse();

    }

    private Livro criarLivro() {
        return Livro.builder().id(null).autor("aline").titulo("java").isbn("121").build();
    }
}
