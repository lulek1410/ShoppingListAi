CREATE SEQUENCE user_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE users (
  id BIGINT NOT NULL DEFAULT nextval('user_sequence'),
  email TEXT NOT NULL,
  password TEXT NOT NULL,
  name TEXT NOT NULL,
  surname TEXT NOT NULL,
  creation_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT user_email_unique UNIQUE (email)
);

CREATE SEQUENCE list_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE lists (
  id BIGINT NOT NULL DEFAULT nextval('list_sequence'),
  title TEXT NOT NULL,
  PRIMARY KEY (id)
);

CREATE SEQUENCE list_item_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE list_items (
  id BIGINT NOT NULL DEFAULT nextval('list_item_sequence'),
  list_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  checked BOOLEAN NOT NULL DEFAULT FALSE,
  item_order INT NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_list FOREIGN KEY (list_id) REFERENCES lists (id) ON DELETE CASCADE
);

CREATE TABLE users_lists (
  user_id BIGINT NOT NULL,
  list_id BIGINT NOT NULL,
  list_order INT NOT NULL,
  PRIMARY KEY (user_id, list_id),
  CONSTRAINT fk_users FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
  CONSTRAINT fk_lists FOREIGN KEY (list_id) REFERENCES lists (id) ON DELETE CASCADE
);



