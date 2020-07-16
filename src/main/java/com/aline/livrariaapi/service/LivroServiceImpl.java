package com.aline.livrariaapi.service;

import com.aline.livrariaapi.exception.BusinessException;
import com.aline.livrariaapi.model.entity.Livro;
import com.aline.livrariaapi.model.repository.LivroRepository;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class LivroServiceImpl implements LivroService {

    private LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Livro save(Livro livro) {
        if (repository.existsByIsbn(livro.getIsbn())) {
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(livro);
    }

    @Override
    public Optional<Livro> findById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void deletarLivro(Livro livro) {
        if (Objects.isNull(livro) || Objects.isNull(livro.getId())) {
            throw new IllegalArgumentException("Informe o livro/id");
        }
        repository.delete(livro);
    }

    @Override
    public Livro atualizarLivro(Livro livro) {
        if (Objects.isNull(livro) || Objects.isNull(livro.getId())) {
            throw new IllegalArgumentException("Informe o livro/id");
        }
        return this.repository.save(livro);
    }
}
