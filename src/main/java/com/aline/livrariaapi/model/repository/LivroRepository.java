package com.aline.livrariaapi.model.repository;

import com.aline.livrariaapi.model.entity.Livro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LivroRepository extends JpaRepository<Livro, Long> {

    boolean existsByIsbn(String isbn);

    Optional<Livro> findById(Long id);
}
