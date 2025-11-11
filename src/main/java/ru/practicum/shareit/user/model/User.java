package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {
    private Long id;

    @NotBlank(message = "Имя не должно быть пустым.")
    private String name;

    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Не корректный Email.")
    private String email;
}
