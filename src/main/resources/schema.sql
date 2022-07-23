-- DROP TABLES
DROP TABLE IF EXISTS users, films, friends, film_likes, film_genres, MPA,
    genres, directors, film_directors;

CREATE TABLE IF NOT EXISTS USERS (
    user_id INTEGER AUTO_INCREMENT(1) PRIMARY KEY,
    email VARCHAR NOT NULL,
    login VARCHAR NOT NULL,
    user_name VARCHAR NOT NULL,
    birthday DATE
    CONSTRAINT invalid_email CHECK (email <> '' AND INSTR(email, '@') > 0),
    CONSTRAINT invalid_login CHECK (login <> '' AND INSTR(login, ' ') = 0),
    CONSTRAINT invalid_birthday CHECK (birthday < CURRENT_DATE)
);

CREATE TABLE IF NOT EXISTS MPA (
    mpa_id INTEGER PRIMARY KEY,
    mpa_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS FILMS (
    film_id INTEGER PRIMARY KEY AUTO_INCREMENT(1),
    film_name VARCHAR NOT NULL,
    description VARCHAR(200),
    release_date DATE NOT NULL,
    duration INTEGER NOT NULL,
    rate INTEGER NOT NULL DEFAULT 0,
    mpa_id INTEGER REFERENCES MPA(mpa_id),
    CONSTRAINT name_empty CHECK (film_name <> ''),
    CONSTRAINT duration_positive CHECK (duration > 0),
    CONSTRAINT release_date_constr CHECK (release_date >= DATE '1895-12-28')
);

CREATE TABLE IF NOT EXISTS GENRES (
    genre_id INTEGER PRIMARY KEY,
    genre_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM_GENRES (
    film_id INT REFERENCES FILMS(film_id) ON DELETE CASCADE,
    genre_id INT REFERENCES GENRES(genre_id),
    CONSTRAINT fg_pk PRIMARY KEY(film_id, genre_id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INTEGER REFERENCES films ON DELETE CASCADE,
    user_id INTEGER REFERENCES users ON DELETE CASCADE,
    CONSTRAINT flikes_pk PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS DIRECTORS (
    id INTEGER PRIMARY KEY AUTO_INCREMENT(1),
    director_name VARCHAR NOT NULL
);

CREATE TABLE IF NOT EXISTS FILM_DIRECTORS (
    film_id INTEGER REFERENCES FILMS(film_id) ON DELETE CASCADE,
    id INTEGER REFERENCES DIRECTORS(id) ON DELETE CASCADE,
    CONSTRAINT film_director_pk PRIMARY KEY (film_id, id)
);

CREATE TABLE IF NOT EXISTS film_likes (
    film_id INTEGER REFERENCES FILMS(film_id) ON DELETE CASCADE,
    user_id INTEGER REFERENCES USERS(user_id) ON DELETE CASCADE,
    CONSTRAINT flikes_pk PRIMARY KEY (film_id, user_id)
);

CREATE TABLE IF NOT EXISTS FRIENDS (
    user_id INTEGER REFERENCES USERS(user_id) ON DELETE CASCADE,
    friend_id INT REFERENCES USERS(user_id) ON DELETE CASCADE,
    status BOOLEAN DEFAULT FALSE,
    CONSTRAINT friends_pk PRIMARY KEY (user_id, friend_id),
    CONSTRAINT self_friend CHECK (user_id <> friend_id)
);


