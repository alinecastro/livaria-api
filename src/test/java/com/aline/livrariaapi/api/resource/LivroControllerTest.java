package com.aline.livrariaapi.api.resource;

import com.aline.livrariaapi.api.dto.LivroDTO;
import com.aline.livrariaapi.exception.BusinessException;
import com.aline.livrariaapi.model.entity.Livro;
import com.aline.livrariaapi.service.LivroService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest
@AutoConfigureMockMvc
public class LivroControllerTest {
    static String LIVRO_API = "/api/livro";

    @Autowired
    MockMvc mvc;

    @MockBean
    LivroService service;

    @Test
    @DisplayName("Deve cadastrar um livro com sucesso.")
     public void criarLivroTest() throws Exception {
        LivroDTO dto = criarNovoLivroDto();

        Livro livro = Livro.builder().id(1L).autor("aline").titulo("java").isbn("121").build();
        BDDMockito.given(service.save(Mockito.any(Livro.class)))
                .willReturn(livro);

        String json = new ObjectMapper().writeValueAsString(dto);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LIVRO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(1L))
                .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(dto.getTitulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("autor").value(dto.getAutor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(dto.getIsbn()));
    }


    @Test
    @DisplayName("Deve lançar erro de validação quando não houver dados suficiente " +
            "para a criação do livro.")
    public void criarLivroInvalidoTest() throws Exception {
        String json = new ObjectMapper().writeValueAsString(new LivroDTO());

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LIVRO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(3)));
    }

    @Test
    @DisplayName("Deve lançar erro quando o isbn informado já existir.")
    public void criarLivroComIsbnDuplicado() throws Exception {
        LivroDTO livroDTO = criarNovoLivroDto();
        String json = new ObjectMapper().writeValueAsString(livroDTO);
        String msnValidacao = "Isbn já cadastrado.";
        BDDMockito
                .given(service.save(Mockito.any(Livro.class)))
                .willThrow(new BusinessException(msnValidacao));

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post(LIVRO_API)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(json);

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("errors", Matchers.hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("errors[0]").value(msnValidacao));
    }

    @Test
    @DisplayName("Deve retornar informações do livro informado.")
    public void getDetalhesDoLivro() throws Exception {
        //cenario (given)
        Long id = 1L;
        Livro livro = Livro.builder()
                .id(id)
                .autor("Monteiro Lobato")
                .isbn("dfasd")
                .titulo("Sitio do pica pau amarelo")
                .build();
        BDDMockito.given(service.findById(id)).willReturn(Optional.of(livro));

        //execução (when)

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LIVRO_API.concat("/".concat(id.toString())))
                .accept(MediaType.APPLICATION_JSON);

        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(livro.getTitulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("autor").value(livro.getAutor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value(livro.getIsbn()));

    }

    @Test
    @DisplayName("Deve retornar resource not found quando o livro não for localizado.")
    public void livroNaoExiste() throws Exception {
        //cenario
        BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

        //execução (when)
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .get(LIVRO_API.concat("/".concat("1")))
                .accept(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve deletar um livro")
    public void deletarLivro() throws Exception {
        //cenario
        BDDMockito
                .given(service.findById(Mockito.anyLong()))
                .willReturn(Optional.of(Livro.builder().id(1L).build()));

        //execuçao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(LIVRO_API.concat("/" + 1));

        //verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test
    @DisplayName("Deve retornar resource not found quando deletar um livro que não existe")
    public void deletarLivroInexistente() throws Exception {
        //cenario
        BDDMockito
                .given(service.findById(Mockito.anyLong()))
                .willReturn(Optional.empty());

        //execuçao
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .delete(LIVRO_API.concat("/" + 1));

        //verificacao
        mvc.perform(request)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test
    @DisplayName("Deve atualizar um livro")
    public void atualizarLivro() throws Exception {
        //cenario
        Long id = 1L;

        String json = new ObjectMapper().writeValueAsString(criarNovoLivroDto());

        Livro livroParaAtualizar = Livro.builder()
                .id(1L)
                .autor("Aline alves")
                .titulo("Java 14")
                .isbn("121")
                .build();

        BDDMockito.given(service.findById(id)).willReturn(Optional.of(livroParaAtualizar));

        Livro livroAtualizado = Livro.builder().id(id).autor("aline").titulo("java").isbn("121").build();
        BDDMockito.given(service.atualizarLivro(livroParaAtualizar)).willReturn(livroAtualizado);
        //execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(LIVRO_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("id").value(id))
                .andExpect(MockMvcResultMatchers.jsonPath("titulo").value(criarNovoLivroDto().getTitulo()))
                .andExpect(MockMvcResultMatchers.jsonPath("autor").value(criarNovoLivroDto().getAutor()))
                .andExpect(MockMvcResultMatchers.jsonPath("isbn").value("121"));

    }

    @Test
    @DisplayName("Deve retornar 404 quando atualizar um livro inexistente")
    public void atualizarLivroInexistente() throws Exception {
        //cenario
        String json = new ObjectMapper().writeValueAsString(criarNovoLivroDto());
        BDDMockito.given(service.findById(Mockito.anyLong())).willReturn(Optional.empty());

        //execucao
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .put(LIVRO_API.concat("/" + 1))
                .content(json)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON);

        //verificacao
        mvc.perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
    
    //metodo para criar um DTO do livro
    private LivroDTO criarNovoLivroDto() {
        return LivroDTO.builder().
                autor("aline").titulo("java").isbn("121").build();
    }
}
