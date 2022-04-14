insert into company
    (company_id, company_name, company_description,	finger_needed, photo_needed,manager_needed)
VALUES
    (1, 'Green', null, true, true, true),
    (2, 'Green2', 'extra company', true, true, true);

SELECT pg_catalog.setval(pg_get_serial_sequence('company', 'company_id'), (SELECT MAX(company_id) FROM company)+1);


insert into project
    (project_id, company_id, project_name, project_description, project_creator_id)
VALUES
    (1, 1, 'test1', null, 1),
    (2, 1, 'test2', null, 1),
    (3, 2, 'test1', null, 2),
    (4, 2, 'test3', null, 2);


SELECT pg_catalog.setval(pg_get_serial_sequence('project', 'project_id'), (SELECT MAX(project_id) FROM project)+1);

insert into users
    (user_id, user_name, login, password, company_id, hours, permissions, boss_id)
VALUES
    (1, 'Михаил Ухлин', 'tipikambr@yandex.ru', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'admin', 0),
    (2, 'Михаил Александров', 'maaleksandrov_1@edu.hse.ru', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 2, 8, 'admin', 0),
    (3, 'test1', 'test', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'admin', 1),
    (4, 'test1', 'test1', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'user', 3),
    (5, 'test2', 'test2', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 1, 8, 'user', 4),
    (6, 'test3', 'test3', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 2, 8, 'suer', 2),
    (7, 'test4', 'test4', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 2, 8, 'suer', 1),
    (8, 'test5', 'test5', 'D8578EDF8458CE06FBC5BB76A58C5CA4', 2, 8, 'suer', 3);

SELECT pg_catalog.setval(pg_get_serial_sequence('users', 'user_id'), (SELECT MAX(user_id) FROM users)+1);


insert into projects_users
    (user_id, project_id)
VALUES
    (1, 1),
    (1, 2),
    (1, 3),
    (1, 4),
    (2, 1),
    (2, 1),
    (3, 1),
    (4, 1);