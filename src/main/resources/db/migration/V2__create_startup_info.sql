-- Default Global Settings
INSERT INTO global_settings (id, code, name, value) VALUES (0, "MULTIUSER_MODE", "Многопользовательский режим", "YES");
INSERT INTO global_settings (id, code, name, value) VALUES (1, "POST_PREMODERATION", "Премодерация постов", "NO");
INSERT INTO global_settings (id, code, name, value) VALUES (2, "STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "YES");

-- Create Admin user
INSERT INTO users (id, is_moderator, reg_time, name, email, password)
    VALUES (3, 1, "2020.07.30", "Admin", "admin@admin.ru", "$2a$10$a5dIKHbA2OXQBViPB13My./lU4PgEwpPdhqo4tew51XCDvmRKlLwG");

-- First id number (hibernate)
INSERT INTO hibernate_sequence (next_val) VALUES (4);
