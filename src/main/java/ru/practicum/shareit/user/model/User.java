package ru.practicum.shareit.user.model;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class User {

    private Long id;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    @NotBlank
    private String name;

    @Email
    @NotEmpty
    private String email;

}
