

INSERT INTO users (email, password, name, surname, creation_date)
VALUES 
  ('testuser1@example.com', 'password1', 'John', 'Doe', '2003-01-10'),
  ('testuser2@example.com', 'password2', 'Jane', 'Smith', '2010-08-08');

INSERT INTO lists (title)
VALUES 
  ( 'Shopping List 1'),
  ( 'Shopping List 2'),
  ( 'Shopping List 3');

INSERT INTO users_lists (list_id, user_id, list_order)
VALUES 
  (1, 1, 1),
  (2, 1, 2),
  (1, 2, 1),
  (3, 2, 2);
  

INSERT INTO list_items ( content, list_id, item_order)
VALUES 
  ( 'Item 1', 1, 1),
  ( 'Item 2', 1, 2),
  ( 'Item 3', 2, 1);
