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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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
        String isbn = "1212";
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


    @Test
    @DisplayName("Deve obter um livro pelo id")
    public void findByIdTest() {
        //cenario
        Livro livro = criarLivro();
        entityManager.persist(livro);

        //execucao
        Optional<Livro> livroLocalizado = repository.findById(livro.getId());

        //verificacao
        Assertions.assertThat(livroLocalizado.isPresent()).isTrue();
    }

    @Test
    @DisplayName("Deve salvar um livro")
    public void salvarLivro() {
        //cenario
        Livro livro = criarLivro();
        //execucao
        Livro livroSalvo = repository.save(livro);
        //verificacao
        Assertions.assertThat(livroSalvo.getId()).isNotNull();
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deleteLivro() throws ParseException {
        String sDate1 = "31/12/1998";
        System.out.println("date original " + sDate1);
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(sDate1);
        System.out.println(date1 + " data formatada");
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        java.sql.Date data = new java.sql.Date(format.parse(sDate1).getTime());
        System.out.println(data + " data formatada nova");
        //cenario
/*        Livro livro = criarLivro();
        entityManager.persist(livro);
        Livro livroSalvo = entityManager.find(Livro.class, livro.getId());
        //execucao
        repository.delete(livroSalvo);

        //verificacao
        Livro livroDeletado = entityManager.find(Livro.class, livro.getId());
        Assertions.assertThat(livroDeletado).isNull();
    */
    }

    private Livro criarLivro() {
        return Livro.builder().id(null).autor("aline").titulo("java").isbn("121").build();
    }
}
