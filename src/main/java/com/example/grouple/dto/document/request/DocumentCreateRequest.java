package com.example.grouple.dto.document.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DocumentCreateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String description;

    @NotBlank
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private Integer size;

    @NotNull
    private Integer organizationId;
}
