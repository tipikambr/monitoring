create table users
(
    user_id     bigserial,
    user_name   text,
    login       text,
    password    text,
    company_id  integer,
    hours       integer,
    permissions text,
    boss_id     bigint,
    PRIMARY KEY (user_id)
);

create table project
(
    project_id          bigserial,
    company_id          integer,
    project_name        text,
    project_description text,
    PRIMARY KEY (project_id)
);

create table task
(
    task_id          bigserial,
    user_id          bigint,
    project_id       bigint,
    task_name        text,
    task_description text,
    start_time       timestamp,
    end_time         timestamp,
    status           text,
    PRIMARY KEY (task_id, user_id)
);

create table activity
(
    activity_id bigserial,
    user_id     bigint,
    start_time  timestamp,
    end_time    timestamp,
    PRIMARY KEY (activity_id, user_id)
);

create table company
(
    company_id          serial,
    company_name        text,
    company_description text,
    finger_needed       boolean,
    photo_needed        boolean,
    manager_needed      boolean,
    PRIMARY KEY (company_id)
);

create table tokens
(
    user_id               bigint,
    token                 text,
    token_endtime         timestamp,
    refresh_token         text,
    refresh_token_endtime timestamp,
    PRIMARY KEY (user_id)
);

create table projects_users
(
    user_id    bigint,
    project_id bigint
)