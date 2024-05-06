package ru.practicum.shareit.user.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDtoRequest {

    @NotBlank
    private String name;

    @Email
    @NotBlank
    @NotNull
    private String email;

}
