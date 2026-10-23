-- ШАГ 1. Выполнить в pgAdmin под суперпользователем postgres (БД postgres).
-- Пароль придумай сам и НЕ коммить этот файл с настоящим паролем:
-- реальный пароль потом пропишешь только в файле .env
CREATE ROLE library_app
    LOGIN
    PASSWORD 'ЗАМЕНИ_НА_СВОЙ_ПАРОЛЬ'
    NOSUPERUSER NOCREATEDB NOCREATEROLE;

-- Затем в pgAdmin: ПКМ по Databases -> Create -> Database... -> имя "library" -> Save.
