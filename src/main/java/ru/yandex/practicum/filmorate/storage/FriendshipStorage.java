package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Friendship;

import java.util.List;

public interface FriendshipStorage {
    void confirmFriendship(int userId, int friendId);
    List<Friendship> getFriendshipsByUserId(int userId);
}