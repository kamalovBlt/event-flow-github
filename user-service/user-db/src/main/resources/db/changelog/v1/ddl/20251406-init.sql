create sequence user_id
    start with 100000
    increment by 1;

create table users
(
    id                bigint  default nextval('user_id'),
    email             varchar(255) not null,
    password          varchar(255),
    first_name        varchar(255) not null,
    last_name         varchar(255),
    is_public_profile boolean default false,
    city              varchar(255),
    deleted           boolean default false,
    auth_provider     varchar(50)  not null,
    ---
    constraint user_id_pk primary key (id),
    constraint email_unique unique (email)
);

comment
    on column users.id is 'ID';
comment
    on column users.password is 'Пароль пользователя';
comment
    on column users.email is 'Почта пользователя';
comment
    on column users.first_name is 'Имя пользователя';
comment
    on column users.last_name is 'Фамилия пользователя';
comment
    on column users.city is 'Город пользователя';
comment
    on column users.is_public_profile is 'Публичный профиль';
comment
    on column users.deleted is 'Проверка на удаление';

create table user_roles
(
    user_id bigint      not null,
    role    varchar(50) not null,
    -------------------------------------------------------------------------------------------
    constraint fk_user_roles_user foreign key (user_id) references users (id) on delete cascade
);

comment on column user_roles.user_id is 'Id пользователя';
comment on column user_roles.role is 'Роль пользователя';
