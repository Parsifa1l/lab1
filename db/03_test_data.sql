-- ШАГ 3 (по желанию). Тестовые записи. Выполнять под postgres в БД library, один раз, на пустых таблицах.
INSERT INTO users (name, email) VALUES
    ('Иван Иванов',     'ivan@example.com'),
    ('Мария Петрова',   'maria@example.com'),
    ('Алексей Смирнов', 'alexey@example.com');

INSERT INTO book_editions (title, author, isbn) VALUES
    ('Война и мир',                 'Л. Н. Толстой',      '978-5-17-090000-1'),
    ('Мастер и Маргарита',          'М. А. Булгаков',     '978-5-17-090000-2'),
    ('Преступление и наказание',    'Ф. М. Достоевский',  '978-5-17-090000-3');

INSERT INTO copies (edition_id, inventory_number, book_condition) VALUES
    (1, 'INV-001', 'GOOD'),
    (1, 'INV-002', 'NEW'),
    (2, 'INV-003', 'WORN'),
    (3, 'INV-004', 'DAMAGED');

-- Мария взяла «Мастера и Маргариту» (экземпляр 3), срок возврата через 10 дней
INSERT INTO loans (copy_id, user_id, loan_date, due_date) VALUES
    (3, 2, now() - interval '4 days', now() + interval '10 days');

-- Иван встал в очередь на «Мастера и Маргариту»
INSERT INTO waiting_requests (edition_id, user_id, request_date, status) VALUES
    (2, 1, now(), 'WAITING');
