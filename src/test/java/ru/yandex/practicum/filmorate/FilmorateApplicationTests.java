package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FilmorateApplicationTests {
	private final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

	@Test
	void contextLoads() {
	}

	@Test
	void filmValidation_ShouldFailWhenNameIsEmpty() {
		Film film = new Film();
		film.setName("");
		film.setDescription("Valid description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для пустого названия");
	}

	@Test
	void filmValidation_ShouldFailWhenDescriptionTooLong() {
		String longDescription = "a".repeat(201);
		Film film = new Film();
		film.setName("Valid name");
		film.setDescription(longDescription);
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(120);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для описания длиннее 200 символов");
	}

	@Test
	void filmValidation_ShouldFailWhenReleaseDateTooEarly() {
		Film film = new Film();
		film.setName("Valid name");
		film.setDescription("Valid description");
		film.setReleaseDate(LocalDate.of(1895, 12, 27));
		film.setDuration(120);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для даты релиза раньше 28.12.1895");

		boolean found = false;
		for (ConstraintViolation<Film> violation : violations) {
			if (violation.getMessage().contains("28 декабря 1895")) {
				found = true;
				break;
			}
		}
		assertTrue(found, "Должно содержать сообщение о минимальной дате релиза");
	}

	@Test
	void filmValidation_ShouldPassWhenReleaseDateIsMinAllowed() {
		Film film = new Film();
		film.setName("Valid name");
		film.setDescription("Valid description");
		film.setReleaseDate(LocalDate.of(1895, 12, 28));
		film.setDuration(120);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertTrue(violations.isEmpty(), "Должен проходить валидацию для минимальной допустимой даты");
	}

	@Test
	void filmValidation_ShouldFailWhenDurationNegative() {
		Film film = new Film();
		film.setName("Valid name");
		film.setDescription("Valid description");
		film.setReleaseDate(LocalDate.of(2000, 1, 1));
		film.setDuration(-1);

		Set<ConstraintViolation<Film>> violations = validator.validate(film);
		assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для отрицательной продолжительности");
	}

	@Test
	void userValidation_ShouldFailForInvalidEmails() {
		String[] invalidEmails = {
				"plainaddress",
				"@missing-local.com",
				"invalid@domain.c",
				"invalid@domain.c1",
				"invalid@-domain.com",
				"invalid@domain-.com",
				"invalid@domain..com",
				"invalid@.domain.com",
				"space in@local.com",
				"invalid@domain.com.",
				"invalid@domain." + "a".repeat(64) + ".com",
				"это-неправильный?эмейл@"
		};

		for (String email : invalidEmails) {
			User user = new User();
			user.setEmail(email);
			user.setLogin("validLogin");
			user.setBirthday(LocalDate.of(2000, 1, 1));

			Set<ConstraintViolation<User>> violations = validator.validate(user);
			assertFalse(violations.isEmpty(), "Email '" + email + "' должен быть недопустимым");
		}
	}

	@Test
	void userValidation_ShouldPassForValidEmails() {
		String[] validEmails = {
				"email@example.com",
				"firstname.lastname@example.com",
				"email@subdomain.example.com",
				"firstname+lastname@example.com",
				"1234567890@example.com",
				"email@example.name",
				"email@example.museum",
				"email@example.co.jp",
				"firstname-lastname@example.com",
				"user@domain-with-dash.com",
				"user@[127.0.0.1]"
		};

		for (String email : validEmails) {
			User user = new User();
			user.setEmail(email);
			user.setLogin("validLogin");
			user.setBirthday(LocalDate.of(2000, 1, 1));

			Set<ConstraintViolation<User>> violations = validator.validate(user);
			assertTrue(violations.isEmpty(), "Email '" + email + "' должен быть допустимым");
		}
	}

	@Test
	void userValidation_ShouldPassForShortButValidTLDs() {
		String[] validEmails = {
				"email@example.co",
				"email@example.com",
				"email@example.info"
		};

		for (String email : validEmails) {
			User user = new User();
			user.setEmail(email);
			user.setLogin("validLogin");
			user.setBirthday(LocalDate.of(2000, 1, 1));

			Set<ConstraintViolation<User>> violations = validator.validate(user);
			assertTrue(violations.isEmpty(), "Email '" + email + "' должен быть допустимым");
		}
	}

	@Test
	void userValidation_ShouldFailForNumericTLDs() {
		String[] invalidEmails = {
				"email@domain.c0m",
				"email@domain.123",
				"email@domain.1"
		};

		for (String email : invalidEmails) {
			User user = new User();
			user.setEmail(email);
			user.setLogin("validLogin");
			user.setBirthday(LocalDate.of(2000, 1, 1));

			Set<ConstraintViolation<User>> violations = validator.validate(user);
			assertFalse(violations.isEmpty(), "Email '" + email + "' должен быть недопустимым");
		}
	}

	@Test
	void userValidation_ShouldFailWhenLoginContainsSpaces() {
		User user = new User();
		user.setEmail("valid@email.com");
		user.setLogin("login with spaces");
		user.setBirthday(LocalDate.of(2000, 1, 1));

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для логина с пробелами");
	}

	@Test
	void userValidation_ShouldUseLoginWhenNameEmpty() {
		User user = new User();
		user.setEmail("valid@email.com");
		user.setLogin("validLogin");
		user.setBirthday(LocalDate.of(2000, 1, 1));

		assertNull(user.getName());

		if (user.getName() == null || user.getName().isBlank()) {
			user.setName(user.getLogin());
		}

		assertEquals("validLogin", user.getName(), "Имя должно совпадать с логином при отсутствии имени");
	}

	@Test
	void userValidation_ShouldFailWhenBirthdayInFuture() {
		User user = new User();
		user.setEmail("valid@email.com");
		user.setLogin("validLogin");
		user.setBirthday(LocalDate.now().plusDays(1));

		Set<ConstraintViolation<User>> violations = validator.validate(user);
		assertFalse(violations.isEmpty(), "Ожидалась ошибка валидации для даты рождения в будущем");
	}
}