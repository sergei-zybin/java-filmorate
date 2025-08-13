package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.MpaRating;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class MpaDbStorage implements MpaStorage {
    private final JdbcTemplate jdbcTemplate;

    public List<MpaRating> getAll() {
        String sql = "SELECT id, name FROM mpa_ratings ORDER BY id";
        return jdbcTemplate.query(sql, this::mapRowToMpaRating);
    }

    public MpaRating getById(int id) {
        String sql = "SELECT id, name FROM mpa_ratings WHERE id = ?";
        return jdbcTemplate.query(sql, this::mapRowToMpaRating, id)
                .stream()
                .findFirst()
                .orElseThrow(() -> new NotFoundException("MPA рейтинг с ID " + id + " не найден"));
    }

    private MpaRating mapRowToMpaRating(ResultSet rs, int rowNum) throws SQLException {
        MpaRating mpa = new MpaRating();
        mpa.setId(rs.getInt("id"));
        mpa.setName(rs.getString("name"));
        return mpa;
    }
}
