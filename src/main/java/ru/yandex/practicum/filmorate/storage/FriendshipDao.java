package ru.yandex.practicum.filmorate.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Friendship;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FriendshipDao {
    private final JdbcTemplate jdbcTemplate;

    public void confirmFriendship(int userId, int friendId) {
        String sql = "UPDATE friendships SET confirmed = true WHERE user_id = ? AND friend_id = ?";
        jdbcTemplate.update(sql, userId, friendId);
    }

    public List<Friendship> getFriendshipsByUserId(int userId) {
        String sql = "SELECT * FROM friendships WHERE user_id = ?";
        return jdbcTemplate.query(sql, this::mapRowToFriendship, userId);
    }

    private Friendship mapRowToFriendship(ResultSet rs, int rowNum) throws SQLException {
        Friendship friendship = new Friendship();
        friendship.setUserId(rs.getInt("user_id"));
        friendship.setFriendId(rs.getInt("friend_id"));
        friendship.setConfirmed(rs.getBoolean("confirmed"));
        return friendship;
    }
}