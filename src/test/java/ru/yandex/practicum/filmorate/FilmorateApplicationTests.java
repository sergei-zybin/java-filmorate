package ru.yandex.practicum.filmorate;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;
import ru.yandex.practicum.filmorate.storage.GenreDao;
import ru.yandex.practicum.filmorate.storage.MpaRatingDao;
import ru.yandex.practicum.filmorate.storage.FriendshipDao;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({UserDbStorage.class, FilmDbStorage.class, GenreDao.class, MpaRatingDao.class, FriendshipDao.class})
class FilmorateApplicationTests {
	private final UserDbStorage userStorage;
	private final FilmDbStorage filmStorage;
	private final GenreDao genreDao;
	private final MpaRatingDao mpaRatingDao;
	private final FriendshipDao friendshipDao;

	@Test
	void testGetUserById() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser = userStorage.create(user);

		User userFromDb = userStorage.getUserById(savedUser.getId());

		assertThat(userFromDb)
				.hasFieldOrPropertyWithValue("id", savedUser.getId())
				.hasFieldOrPropertyWithValue("email", "test@example.com")
				.hasFieldOrPropertyWithValue("login", "testLogin")
				.hasFieldOrPropertyWithValue("name", "testLogin");
	}

	@Test
	void testGetUserByIdNotFound() {
		assertThrows(NotFoundException.class, () -> userStorage.getUserById(999));
	}

	@Test
	void testCreateUser() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser = userStorage.create(user);

		assertThat(savedUser.getId()).isNotZero();
		assertThat(userStorage.getUserById(savedUser.getId()))
				.hasFieldOrPropertyWithValue("email", "test@example.com");
	}

	@Test
	void testUpdateUser() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser = userStorage.create(user);

		savedUser.setEmail("updated@example.com");
		savedUser.setName("UpdatedName");
		userStorage.update(savedUser);

		User updatedUser = userStorage.getUserById(savedUser.getId());
		assertThat(updatedUser)
				.hasFieldOrPropertyWithValue("email", "updated@example.com")
				.hasFieldOrPropertyWithValue("name", "UpdatedName");
	}

	@Test
	void testGetAllUsers() {
		User user1 = new User();
		user1.setEmail("test1@example.com");
		user1.setLogin("testLogin1");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.create(user1);

		User user2 = new User();
		user2.setEmail("test2@example.com");
		user2.setLogin("testLogin2");
		user2.setBirthday(LocalDate.of(2000, 1, 1));
		userStorage.create(user2);

		List<User> users = userStorage.getAll();

		assertThat(users).hasSize(2);
	}

	@Test
	void testAddAndRemoveFriend() {
		User user1 = new User();
		user1.setEmail("test1@example.com");
		user1.setLogin("testLogin1");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser1 = userStorage.create(user1);

		User user2 = new User();
		user2.setEmail("test2@example.com");
		user2.setLogin("testLogin2");
		user2.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser2 = userStorage.create(user2);

		userStorage.addFriend(savedUser1.getId(), savedUser2.getId());
		friendshipDao.confirmFriendship(savedUser1.getId(), savedUser2.getId());

		assertThat(userStorage.getFriends(savedUser1.getId())).contains(savedUser2.getId());

		userStorage.removeFriend(savedUser1.getId(), savedUser2.getId());
		assertThat(userStorage.getFriends(savedUser1.getId())).isEmpty();
	}

	@Test
	void testGetFilmById() {
		MpaRating mpa = mpaRatingDao.getById(1);
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		film.setMpa(mpa);
		Film savedFilm = filmStorage.create(film);

		Film filmFromDb = filmStorage.getFilmById(savedFilm.getId());

		assertThat(filmFromDb)
				.hasFieldOrPropertyWithValue("id", savedFilm.getId())
				.hasFieldOrPropertyWithValue("name", "Test Film");
	}

	@Test
	void testGetFilmByIdNotFound() {
		assertThrows(NotFoundException.class, () -> filmStorage.getFilmById(999));
	}

	@Test
	void testCreateFilm() {
		MpaRating mpa = mpaRatingDao.getById(1);
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		film.setMpa(mpa);
		Film savedFilm = filmStorage.create(film);

		assertThat(savedFilm.getId()).isNotZero();
		assertThat(filmStorage.getFilmById(savedFilm.getId()))
				.hasFieldOrPropertyWithValue("name", "Test Film");
	}

	@Test
	void testUpdateFilm() {
		MpaRating mpa = mpaRatingDao.getById(1);
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		film.setMpa(mpa);
		Film savedFilm = filmStorage.create(film);

		savedFilm.setName("Updated Film");
		filmStorage.update(savedFilm);

		Film updatedFilm = filmStorage.getFilmById(savedFilm.getId());
		assertThat(updatedFilm)
				.hasFieldOrPropertyWithValue("name", "Updated Film");
	}

	@Test
	void testGetAllFilms() {
		MpaRating mpa = mpaRatingDao.getById(1);
		Film film1 = new Film();
		film1.setName("Test Film 1");
		film1.setDescription("Description 1");
		film1.setReleaseDate(LocalDate.of(2000, 1, 1));
		film1.setDuration(120);
		film1.setMpa(mpa);
		filmStorage.create(film1);

		Film film2 = new Film();
		film2.setName("Test Film 2");
		film2.setDescription("Description 2");
		film2.setReleaseDate(LocalDate.of(2000, 1, 1));
		film2.setDuration(120);
		film2.setMpa(mpa);
		filmStorage.create(film2);

		List<Film> films = filmStorage.getAll();

		assertThat(films).hasSize(2);
	}

	@Test
	void testAddAndRemoveLike() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser = userStorage.create(user);

		MpaRating mpa = mpaRatingDao.getById(1);
		Film film = new Film();
		film.setName("Test Film");
		film.setDescription("Description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);
		film.setMpa(mpa);
		Film savedFilm = filmStorage.create(film);

		filmStorage.addLike(savedFilm.getId(), savedUser.getId());
		Film likedFilm = filmStorage.getFilmById(savedFilm.getId());
		assertThat(likedFilm.getLikes()).contains(savedUser.getId());

		filmStorage.removeLike(savedFilm.getId(), savedUser.getId());
		Film unlikedFilm = filmStorage.getFilmById(savedFilm.getId());
		assertThat(unlikedFilm.getLikes()).isEmpty();
	}

	@Test
	void testGetPopularFilms() {
		User user = new User();
		user.setEmail("test@example.com");
		user.setLogin("testLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser = userStorage.create(user);

		MpaRating mpa = mpaRatingDao.getById(1);
		Film film1 = new Film();
		film1.setName("Popular Film");
		film1.setDescription("Description 1");
		film1.setReleaseDate(LocalDate.of(2000, 1, 1));
		film1.setDuration(120);
		film1.setMpa(mpa);
		Film savedFilm1 = filmStorage.create(film1);

		Film film2 = new Film();
		film2.setName("Less Popular Film");
		film2.setDescription("Description 2");
		film2.setReleaseDate(LocalDate.of(2000, 1, 1));
		film2.setDuration(120);
		film2.setMpa(mpa);
		Film savedFilm2 = filmStorage.create(film2);

		filmStorage.addLike(savedFilm1.getId(), savedUser.getId());

		List<Film> popularFilms = filmStorage.getPopularFilms(2);

		assertThat(popularFilms).hasSize(2);
		assertThat(popularFilms.get(0).getName()).isEqualTo("Popular Film");
		assertThat(popularFilms.get(0).getLikes()).contains(savedUser.getId());
	}

	@Test
	void testGetAllGenres() {
		List<Genre> genres = genreDao.getAll();
		assertThat(genres).hasSize(6); // Ожидаем 6 жанров из data.sql
	}

	@Test
	void testGetGenreById() {
		Genre genre = genreDao.getById(1);
		assertThat(genre)
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "Комедия");
	}

	@Test
	void testGetGenreByIdNotFound() {
		assertThrows(NotFoundException.class, () -> genreDao.getById(999));
	}

	@Test
	void testGetAllMpaRatings() {
		List<MpaRating> mpaRatings = mpaRatingDao.getAll();
		assertThat(mpaRatings).hasSize(5); // Ожидаем 5 рейтингов из data.sql
	}

	@Test
	void testGetMpaRatingById() {
		MpaRating mpa = mpaRatingDao.getById(1);
		assertThat(mpa)
				.hasFieldOrPropertyWithValue("id", 1)
				.hasFieldOrPropertyWithValue("name", "G");
	}

	@Test
	void testGetMpaRatingByIdNotFound() {
		assertThrows(NotFoundException.class, () -> mpaRatingDao.getById(999));
	}

	@Test
	void testConfirmFriendship() {
		User user1 = new User();
		user1.setEmail("test1@example.com");
		user1.setLogin("testLogin1");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser1 = userStorage.create(user1);

		User user2 = new User();
		user2.setEmail("test2@example.com");
		user2.setLogin("testLogin2");
		user2.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser2 = userStorage.create(user2);

		userStorage.addFriend(savedUser1.getId(), savedUser2.getId());
		friendshipDao.confirmFriendship(savedUser1.getId(), savedUser2.getId());

		List<Friendship> friendships = friendshipDao.getFriendshipsByUserId(savedUser1.getId());
		assertThat(friendships).hasSize(1);
		assertThat(friendships.get(0).isConfirmed()).isTrue();
	}

	@Test
	void testGetFriendshipsByUserId() {
		User user1 = new User();
		user1.setEmail("test1@example.com");
		user1.setLogin("testLogin1");
		user1.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser1 = userStorage.create(user1);

		User user2 = new User();
		user2.setEmail("test2@example.com");
		user2.setLogin("testLogin2");
		user2.setBirthday(LocalDate.of(2000, 1, 1));
		User savedUser2 = userStorage.create(user2);

		userStorage.addFriend(savedUser1.getId(), savedUser2.getId());

		List<Friendship> friendships = friendshipDao.getFriendshipsByUserId(savedUser1.getId());
		assertThat(friendships).hasSize(1);
		assertThat(friendships.get(0).getFriendId()).isEqualTo(savedUser2.getId());
	}
}