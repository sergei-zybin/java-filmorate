# java-filmorate
DB diagram:

https://github.com/sergei-zybin/java-filmorate/blob/add-database/db_diagram_final.png

> https://dbdiagram.io/d

```sql
Table users {
  id integer [primary key, increment]
  email varchar [unique, not null]
  login varchar [unique, not null]
  name varchar
  birthday date [not null]
}

Table films {
  id integer [primary key, increment]
  name varchar [not null]
  description varchar(200) [not null]
  release_date date [not null]
  duration integer [not null]
  mpa_id integer [not null]
}

Table mpa_ratings {
  id integer [primary key, increment]
  name varchar [unique, not null]
}

Table genres {
  id integer [primary key, increment]
  name varchar [unique, not null]
}

Table film_genres {
  film_id integer [not null]
  genre_id integer [not null]
}

Table likes {
  film_id integer [not null]
  user_id integer [not null]
}

Table friendships {
  user_id integer [not null]
  friend_id integer [not null]
  confirmed boolean [not null, default: false]
}

Ref: films.mpa_id > mpa_ratings.id
Ref: film_genres.film_id > films.id
Ref: film_genres.genre_id > genres.id
Ref: likes.film_id > films.id
Ref: likes.user_id > users.id
Ref: friendships.user_id > users.id
Ref: friendships.friend_id > users.id
Ref: "film_genres"."film_id" < "film_genres"."genre_id"
