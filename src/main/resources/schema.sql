DROP TABLE IF EXISTS payment_transactions;
DROP TABLE IF EXISTS watch_history;
DROP TABLE IF EXISTS favorites;
DROP TABLE IF EXISTS ratings;
DROP TABLE IF EXISTS comments;
DROP TABLE IF EXISTS movie_directors;
DROP TABLE IF EXISTS movie_actors;
DROP TABLE IF EXISTS movie_genres;
DROP TABLE IF EXISTS episodes;
DROP TABLE IF EXISTS banners;
DROP TABLE IF EXISTS movies;
DROP TABLE IF EXISTS directors;
DROP TABLE IF EXISTS actors;
DROP TABLE IF EXISTS genres;
DROP TABLE IF EXISTS countries;
DROP TABLE IF EXISTS user_subscriptions;
DROP TABLE IF EXISTS vouchers;
DROP TABLE IF EXISTS subscription_plans;
DROP TABLE IF EXISTS payment_transactions;
DROP TABLE IF EXISTS wallet_transactions;
DROP TABLE IF EXISTS wallets;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS roles;

CREATE TABLE roles (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(30) NOT NULL UNIQUE,
    description VARCHAR(120) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    avatar_url VARCHAR(255),
    enabled BIT NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_users_email (email)
);

CREATE TABLE user_roles (
    user_id BIGINT NOT NULL,
    role_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_user_roles_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_roles_role FOREIGN KEY (role_id) REFERENCES roles(id)
);

CREATE TABLE wallets (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL UNIQUE,
    balance DECIMAL(15,2) NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_wallet_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE wallet_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    wallet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(40) NOT NULL,
    status VARCHAR(30) NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    balance_after DECIMAL(15,2) NOT NULL,
    reference_code VARCHAR(80) NOT NULL,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_wallet_tx_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id),
    CONSTRAINT fk_wallet_tx_user FOREIGN KEY (user_id) REFERENCES users(id),
    INDEX idx_wallet_tx_user_created (user_id, created_at)
);

CREATE TABLE payment_transactions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    wallet_transaction_id BIGINT NOT NULL,
    amount DECIMAL(15,2) NOT NULL,
    provider VARCHAR(80) NOT NULL,
    external_reference VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_payment_wallet_tx FOREIGN KEY (wallet_transaction_id) REFERENCES wallet_transactions(id)
);

CREATE TABLE subscription_plans (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    access_level VARCHAR(20) NOT NULL,
    price DECIMAL(15,2) NOT NULL,
    duration_days INT NOT NULL,
    active BIT NOT NULL DEFAULT b'1',
    feature_description TEXT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);


CREATE TABLE vouchers (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(150) NOT NULL,
    description VARCHAR(255),
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(15,2) NOT NULL,
    max_discount_amount DECIMAL(15,2),
    min_order_amount DECIMAL(15,2) NOT NULL DEFAULT 0,
    quantity INT NOT NULL DEFAULT 0,
    used_count INT NOT NULL DEFAULT 0,
    active BIT NOT NULL DEFAULT b'1',
    start_at DATETIME NOT NULL,
    end_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE user_subscriptions (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    plan_id BIGINT NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    status VARCHAR(20) NOT NULL,
    paid_amount DECIMAL(15,2) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_user_sub_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_user_sub_plan FOREIGN KEY (plan_id) REFERENCES subscription_plans(id),
    INDEX idx_user_sub_user_status (user_id, status)
);

CREATE TABLE countries (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE genres (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE actors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    avatar_url VARCHAR(255),
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE directors (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL UNIQUE,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE movies (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(200) NOT NULL,
    original_title VARCHAR(200),
    slug VARCHAR(220) NOT NULL UNIQUE,
    short_description VARCHAR(500),
    description TEXT,
    release_year INT NOT NULL,
    duration_minutes INT NOT NULL,
    movie_type VARCHAR(20) NOT NULL,
    access_level VARCHAR(20) NOT NULL,
    country_id BIGINT,
    poster_url VARCHAR(255),
    backdrop_url VARCHAR(255),
    trailer_url VARCHAR(255),
    featured BIT NOT NULL DEFAULT b'0',
    popular BIT NOT NULL DEFAULT b'0',
    active BIT NOT NULL DEFAULT b'1',
    average_rating DOUBLE NOT NULL DEFAULT 0,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_movie_country FOREIGN KEY (country_id) REFERENCES countries(id),
    INDEX idx_movies_access_created (access_level, created_at),
    INDEX idx_movies_release_year (release_year)
);

CREATE TABLE banners (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(150) NOT NULL,
    subtitle VARCHAR(255),
    image_url VARCHAR(255) NOT NULL,
    cta_text VARCHAR(100),
    cta_link VARCHAR(255),
    display_order INT NOT NULL DEFAULT 0,
    active BIT NOT NULL DEFAULT b'1',
    movie_id BIGINT,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_banner_movie FOREIGN KEY (movie_id) REFERENCES movies(id)
);

CREATE TABLE episodes (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    episode_number INT NOT NULL,
    title VARCHAR(150) NOT NULL,
    video_url VARCHAR(255) NOT NULL,
    duration_minutes INT NOT NULL,
    free_preview BIT NOT NULL DEFAULT b'0',
    active BIT NOT NULL DEFAULT b'1',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_episode_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    UNIQUE KEY uk_episode_movie_number (movie_id, episode_number)
);

CREATE TABLE movie_genres (
    movie_id BIGINT NOT NULL,
    genre_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, genre_id),
    CONSTRAINT fk_movie_genre_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_movie_genre_genre FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE movie_actors (
    movie_id BIGINT NOT NULL,
    actor_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, actor_id),
    CONSTRAINT fk_movie_actor_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_movie_actor_actor FOREIGN KEY (actor_id) REFERENCES actors(id)
);

CREATE TABLE movie_directors (
    movie_id BIGINT NOT NULL,
    director_id BIGINT NOT NULL,
    PRIMARY KEY (movie_id, director_id),
    CONSTRAINT fk_movie_director_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_movie_director_director FOREIGN KEY (director_id) REFERENCES directors(id)
);

CREATE TABLE comments (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    hidden BIT NOT NULL DEFAULT b'0',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_comment_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_comment_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE ratings (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    movie_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    stars INT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_rating_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_rating_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT ck_rating_stars CHECK (stars BETWEEN 1 AND 5),
    UNIQUE KEY uk_rating_user_movie (user_id, movie_id)
);

CREATE TABLE favorites (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_favorite_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    UNIQUE KEY uk_favorite_user_movie (user_id, movie_id)
);

CREATE TABLE watch_history (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    movie_id BIGINT NOT NULL,
    episode_id BIGINT NOT NULL,
    last_position_seconds INT NOT NULL DEFAULT 0,
    completed BIT NOT NULL DEFAULT b'0',
    last_watched_at DATETIME NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    CONSTRAINT fk_watch_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_watch_movie FOREIGN KEY (movie_id) REFERENCES movies(id),
    CONSTRAINT fk_watch_episode FOREIGN KEY (episode_id) REFERENCES episodes(id),
    UNIQUE KEY uk_watch_user_episode (user_id, episode_id)
);
