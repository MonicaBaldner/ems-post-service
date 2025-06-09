package com.algaworks.algaposts.ems_post_service.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostInput {

    @NotNull
    private String title;

    @NotBlank
    private String body;

    @NotNull
    private String author;

}
