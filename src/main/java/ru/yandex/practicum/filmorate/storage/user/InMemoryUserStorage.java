package ru.yandex.practicum.filmorate.storage.user;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Friendship;
import ru.yandex.practicum.filmorate.model.User;
import java.util.*;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Integer, User> users = new HashMap<>();
    private final Map<String, Friendship> friendships = new HashMap<>();
    private int nextId = 1;

    @Override
    public User create(User user) {
        normalizeName(user);
        user.setId(nextId++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        normalizeName(user);
        if (!users.containsKey(user.getId())) {
            throw new NotFoundException("Пользователь с ID " + user.getId() + " не найден");
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(int id) {
        User user = users.get(id);
        if (user == null) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        return user;
    }

    @Override
    public void addFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);

        if (userId == friendId) {
            throw new ValidationException("Нельзя добавить себя в друзья");
        }

        String key = userId + ":" + friendId;
        if (friendships.containsKey(key)) {
            return;
        }

        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setConfirmed(false);
        friendships.put(key, friendship);
    }

    @Override
    public void removeFriend(int userId, int friendId) {
        getUserById(userId);
        getUserById(friendId);

        String key = userId + ":" + friendId;
        friendships.remove(key);
    }

    @Override
    public Set<Integer> getFriends(int userId) {
        Set<Integer> friendIds = new HashSet<>();
        for (Friendship f : friendships.values()) {
            if (f.getUserId() == userId && f.isConfirmed()) {
                friendIds.add(f.getFriendId());
            }
        }
        return friendIds;
    }

    private void normalizeName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
    }
}