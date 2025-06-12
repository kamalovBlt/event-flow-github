create sequence user_id
    start with 10000
    increment by 1;

create table users
(
    id                bigint default nextval('user_id'),
    email             varchar(255) not null,
    password          varchar(255) not null,
    first_name        varchar(255) not null,
    last_name         varchar(255),
    is_public_profile boolean default false,
    deleted           boolean default false,
    role              varchar(50) not null,
    auth_provider     varchar(50) not null,
    ---
    constraint user_id_pk           primary key (id),
    constraint email_unique         unique (email)
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
    on column users.is_public_profile is 'Публичный профиль';
comment
    on column users.deleted is 'Проверка на удаление';
comment
    on column users.role is 'Роль пользователя';



create table favorite_event
(
    user_id  bigint,
    event_id bigint, -- from Event Service
    ---
    constraint favorite_event_user_id_fk foreign key (user_id) references users (id),
    constraint favorite_event_event_id_pk primary key (user_id, event_id)
);

create table user_friends
(
    user_id   bigint,
    friend_id bigint,
    ---
    constraint user_friends_user_id_fk foreign key (user_id) references users (id),
    constraint user_friends_friend_id_fk foreign key (friend_id) references users (id),
    constraint user_friends_id_pk primary key (user_id, friend_id)
);

CREATE TABLE favorite_location
(
    user_id  bigint,
    location_id bigint, -- from location Service
    ---
    constraint favorite_location_user_id_fk foreign key (user_id) references users (id),
    constraint favorite_location_location_id_pk primary key (user_id, location_id)
);