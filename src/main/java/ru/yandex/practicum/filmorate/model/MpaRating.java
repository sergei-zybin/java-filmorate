package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MpaRating {
    private int id;

    @NotBlank(message = "Рейтинга MPA не может быть пустым")
    private String name;
}