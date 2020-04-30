package com.aline.livrariaapi.service;

import com.aline.livrariaapi.exception.BusinessException;
import com.aline.livrariaapi.model.entity.Livro;
import com.aline.livrariaapi.model.repository.LivroRepository;
import org.springframework.stereotype.Service;

@Service
public class LivroServiceImpl implements LivroService {

    private LivroRepository repository;

    public LivroServiceImpl(LivroRepository repository) {
        this.repository = repository;
    }

    @Override
    public Livro save(Livro livro) {
        if(repository.existsByIsbn(livro.getIsbn())){
            throw new BusinessException("Isbn já cadastrado.");
        }
        return repository.save(livro);
    }
}
