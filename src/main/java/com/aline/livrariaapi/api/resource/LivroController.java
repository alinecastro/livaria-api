package com.aline.livrariaapi.api.resource;

import com.aline.livrariaapi.api.dto.LivroDTO;
import com.aline.livrariaapi.api.exceptions.ApiErrors;
import com.aline.livrariaapi.exception.BusinessException;
import com.aline.livrariaapi.model.entity.Livro;
import com.aline.livrariaapi.service.LivroService;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/livro")
public class LivroController {

    private LivroService livroService;
    private ModelMapper modelMapper;

    public LivroController(LivroService livroService, ModelMapper modelMapper) {
        this.livroService = livroService;
        this.modelMapper = modelMapper;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public LivroDTO criarLivro(@RequestBody @Valid LivroDTO livroDTO) {
        Livro livro = modelMapper.map(livroDTO, Livro.class);

        livro = livroService.save(livro);

        return modelMapper.map(livro, LivroDTO.class);
    }

    @GetMapping("{id}")
    @ResponseStatus(HttpStatus.OK)
    public LivroDTO getDetalheDoLivro(@PathVariable Long id) {
        return livroService.findById(id)
                .map(livro -> modelMapper.map(livro, LivroDTO.class))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletarLivro(@PathVariable Long id) {
        Livro livro = livroService.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        livroService.deletarLivro(livro);
    }

    @PutMapping("{id}")
    public LivroDTO atualizarLivro(@PathVariable Long id, @RequestBody @Valid LivroDTO livroDTO) {
        return livroService.findById(id)
                .map(livro -> {

                    livro.setAutor(livroDTO.getAutor());
                    livro.setTitulo(livroDTO.getTitulo());
                    livro = livroService.atualizarLivro(livro);
                    return modelMapper.map(livro, LivroDTO.class);

                }).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleValidationExceptions(MethodArgumentNotValidException exception) {
        BindingResult bindingResult = exception.getBindingResult();
        return new ApiErrors(bindingResult);
    }

    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrors handleBusinessException(BusinessException e) {
        return new ApiErrors(e);
    }

    //  @ExceptionHandler(ResourceNotFoundException )
}
