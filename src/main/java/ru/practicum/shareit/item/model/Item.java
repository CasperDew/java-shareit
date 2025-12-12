package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */
@Data
@Entity
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank(message = "Название не может быть пустым.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Описание не может быть пустым.")
    @Column(name = "description", nullable = false)
    private String description;

    @NotNull(message = "Статус не может быть пустой.")
    @Column(name = "available", nullable = false)
    private Boolean available;

    @NotNull(message = "Пользователь не может быть пустым.")
    @Column(name = "user_id", nullable = false)
    private Long owner;
}
