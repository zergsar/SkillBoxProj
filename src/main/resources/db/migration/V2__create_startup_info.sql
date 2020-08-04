-- Default Global Settings
INSERT INTO global_settings
(id, code, name, value) VALUES (0, "MULTIUSER_MODE", "Многопользовательский режим", "YES");
INSERT INTO global_settings
(id, code, name, value) VALUES (1, "POST_PREMODERATION", "Премодерация постов", "NO");
INSERT INTO global_settings
(id, code, name, value) VALUES (2, "STATISTICS_IS_PUBLIC", "Показывать всем статистику блога", "YES");

-- Create Admin user
INSERT INTO users
(id, is_moderator, reg_time, name, email, password)
VALUES (3, 1, "2020.07.30", "Admin", "admin@admin.ru", "$2a$10$a5dIKHbA2OXQBViPB13My./lU4PgEwpPdhqo4tew51XCDvmRKlLwG");

-- Create user pass 123456
INSERT INTO users
(id, code, email, is_moderator, name, password, photo, reg_time)
VALUES(5, NULL, 'asd@asd.ru', 0, 'Sed', '$2a$10$tQ7IwsUg7P/PafzxwPb5A.eLd/QeCtONjr7dkQoOjVxDjRYkw.kr.', NULL, '2020-08-03 11:16:30.773000000');
  
-- Create posts
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(9, 1, 'ACCEPTED', 'trrwtwtdttft sd', '2020-08-03 00:00:00', 'Test', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(14, 1, 'ACCEPTED', 'aaasd', '2020-05-10 00:00:00', 'Test123', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(15, 1, 'ACCEPTED', 'scvv3r42342', '2020-06-11 00:00:00', 'Testqwe', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(16, 1, 'ACCEPTED', '21312edsds', '2020-08-12 00:00:00', 'asd', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(17, 1, 'ACCEPTED', '123rfsdfs', '2020-10-01 00:00:00', 'Tesxczt', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(18, 1, 'ACCEPTED', '2sdf12', '2020-03-09 00:00:00', 'asd', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(19, 1, 'ACCEPTED', '3sdfsdc', '2020-03-09 00:00:00', 'cxz', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(20, 1, 'ACCEPTED', 'sd', '2020-03-09 00:00:00', 'qwe', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(21, 1, 'ACCEPTED', 'asd', '2020-03-09 00:00:00', 'c', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(22, 1, 'ACCEPTED', 'trrwtwtdttft sd', '2020-03-09 00:00:00', 'qwe', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(23, 1, 'ACCEPTED', '423', '2020-03-09 00:00:00', 'daasd', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(24, 1, 'ACCEPTED', 'ed', '2020-03-09 00:00:00', 'fgghfgj', 0, 3, 3);
INSERT INTO posts
(id, is_active, moderation_status, text, time, title, view_count, moderator_id, user_id)
VALUES(25, 1, 'ACCEPTED', 'sa', '2020-03-09 00:00:00', 'jjj', 0, 3, 3);


-- Create post_comments
INSERT INTO post_comments
(id, parent_id, text, time, post_id, user_id)
VALUES(12, NULL, 'uuu', '2020-08-03 00:00:00', 9, 3);
INSERT INTO post_comments
(id, parent_id, text, time, post_id, user_id)
VALUES(13, NULL, 'qwe', '2020-08-03 12:00:00', 14, 5);


-- Create post_votes
INSERT INTO post_votes
(id, time, value, post_id, user_id)
VALUES(10, '2020-03-08 13:09:00', 1, 9, 5);
INSERT INTO post_votes
(id, time, value, post_id, user_id)
VALUES(11, '2020-03-08 13:15:00', 1, 9, 3);


-- First id number (hibernate)
INSERT INTO hibernate_sequence (next_val) VALUES (27);