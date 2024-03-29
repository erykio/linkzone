package io.eryk.linkzone.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class PostCreateMedia {

    @NotBlank
    private String title;
}
