DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS users (

    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name varchar(255) NOT NULL,
    email varchar(512) NOT NULL,

    CONSTRAINT uq_user_email UNIQUE (email)

);

CREATE TABLE IF NOT EXISTS items (

    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY NOT NULL,
    name varchar(255) NOT NULL,
    description varchar(255) NOT NULL,
    available BOOLEAN NOT NULL,
    user_id BIGINT,

    CONSTRAINT fk_items_to_users FOREIGN KEY(user_id) REFERENCES users(id)

);
