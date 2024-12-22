DROP TABLE IF EXISTS users CASCADE;
CREATE TABLE users (
    user_id         BIGSERIAL   PRIMARY KEY,
	email           VARCHAR     NOT NULL UNIQUE,
	user_password   VARCHAR     NOT NULL UNIQUE,
	user_name       VARCHAR,
	surname         VARCHAR,
    card_code       VARCHAR,
    user_status     VARCHAR,
    owner_score     SMALLINT CHECK (owner_score IS NULL OR owner_score BETWEEN 1 AND 10),
    user_role       VARCHAR
);

DROP TABLE IF EXISTS posts CASCADE;
CREATE TABLE posts (
	post_id         BIGSERIAL   PRIMARY KEY,

    --land
    owner_id        BIGINT NOT NULL,
    title           VARCHAR,
    description     VARCHAR,
    total_area      INTEGER,

    --address
    street_number   VARCHAR,
    street          VARCHAR,
    city            VARCHAR,
    country_state   VARCHAR,
    postal_code     VARCHAR,
    --
    land_type    VARCHAR,
    --
	cost_per_day    REAL,
	min_date        DATE,
    max_date        DATE,
    score           SMALLINT CHECK (score IS NULL OR score BETWEEN 1 AND 10),

    post_status     VARCHAR,

    image_keys       VARCHAR[],

    FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS bookings CASCADE;
CREATE TABLE bookings (
	booking_id  BIGSERIAL   PRIMARY KEY,

    --bookingDetails
    post_id         BIGINT,
    user_id         BIGINT,
    start_date      DATE,
    end_date        DATE,
    --
    booking_date    DATE,
    total_cost      REAL,
    booking_status  VARCHAR,

    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

DROP TABLE IF EXISTS reviews CASCADE;
CREATE TABLE reviews (
	review_id       BIGSERIAL   PRIMARY KEY,

    author_id       BIGINT,
    post_id         BIGINT,
    booking_id      BIGINT,
    author_name     VARCHAR,
    description     VARCHAR,
    rate            SMALLINT CHECK (rate  BETWEEN 1 AND 10),
    review_status   VARCHAR,

    FOREIGN KEY (author_id) REFERENCES users(user_id),
    FOREIGN KEY (post_id) REFERENCES posts(post_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);

DROP TABLE IF EXISTS reports CASCADE;
CREATE TABLE reports (
	report_id   BIGSERIAL   PRIMARY KEY,

    author_id   BIGINT,
    post_id     BIGINT,
    description VARCHAR
);