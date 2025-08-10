package ru.yandex.practicum.filmorate.service;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import java.util.*;

@Service
public class UserService {
    private final UserStorage userStorage;

    public UserService(@Qualifier("userDbStorage") UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public User create(User user) {
        return userStorage.create(user);
    }

    public User update(User user) {
        return userStorage.update(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getUserById(int id) {
        return userStorage.getUserById(id);
    }

    public void addFriend(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userStorage.addFriend(userId, friendId);
    }

    public void removeFriend(int userId, int friendId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(friendId);
        userStorage.removeFriend(userId, friendId);
    }

    public List<User> getFriends(int userId) {
        userStorage.getUserById(userId);
        Set<Integer> friendIds = userStorage.getFriends(userId);
        List<User> friends = new ArrayList<>();
        for (Integer id : friendIds) {
            friends.add(userStorage.getUserById(id));
        }
        return friends;
    }

    public List<User> getCommonFriends(int userId, int otherId) {
        userStorage.getUserById(userId);
        userStorage.getUserById(otherId);
        Set<Integer> userFriends = userStorage.getFriends(userId);
        Set<Integer> otherFriends = userStorage.getFriends(otherId);
        Set<Integer> commonIds = new HashSet<>(userFriends);
        commonIds.retainAll(otherFriends);
        List<User> commonFriends = new ArrayList<>();
        for (Integer id : commonIds) {
            commonFriends.add(userStorage.getUserById(id));
        }
        return commonFriends;
    }
}