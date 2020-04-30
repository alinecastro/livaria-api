package com.aline.livrariaapi.api.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LivroDTO {

    private Long id;
    @NotEmpty
    private String titulo;
    @NotEmpty
    private String autor;
    @NotEmpty
    private String isbn;
}
