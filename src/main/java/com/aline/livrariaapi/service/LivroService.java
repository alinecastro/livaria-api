package com.aline.livrariaapi.service;

import com.aline.livrariaapi.model.entity.Livro;

import java.util.Optional;

public interface LivroService {
    Livro save(Livro livro);

    Optional<Livro> findById(Long id);

    void deletarLivro(Livro livro);

    Livro atualizarLivro(Livro livro);
}
