insert into company
    (company_id, company_name, company_description,	finger_needed, photo_needed,manager_needed)
VALUES
    (1, 'Green', null, true, true, true),
    (2, 'Green2', 'extra company', true, true, true)

insert into users
    (user_id, user_name, login, password, company_id, hours, permissions, boss_id)
VALUES
    (1, 'Михаил Ухлин', 'tipikambr@yandex.ru', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'admin', 0),
    (2, 'Михаил Александров', 'maaleksandrov_1@edu.hse.ru', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 2, 8, 'admin', 0),
    (3, 'test1', 'test', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'admin', 1),
    (4, 'test1', 'test1', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'user', 3),
    (5, 'test2', 'test2', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'user', 4),
    (6, 'test3', 'test3', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 2, 8, 'suer', 2)
