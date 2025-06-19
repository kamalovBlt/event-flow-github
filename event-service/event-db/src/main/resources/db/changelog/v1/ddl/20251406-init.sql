create sequence artist_id
    start with 100000
    increment by 1;

create table artist
(
    id          bigint  default nextval('artist_id'),
    first_name  varchar(255) not null,
    last_name   varchar(255) not null,
    nickname    varchar(255),
    description text,
    deleted     boolean default false,
    creator_id  bigint       not null,
    ---
    constraint artist_id_pk primary key (id),
    constraint artist_nickname_unique unique (nickname)

);

comment
    on column artist.id is 'ID артиста';
comment
    on column artist.first_name is 'Имя артиста';
comment
    on column artist.last_name is 'Фамилия артиста';
comment
    on column artist.nickname is 'Псевдоним артиста';
comment
    on column artist.description is 'Описание артиста';
comment
    on column artist.creator_id is 'ID создателя артиста (того кто добавил)';

alter table artist
    add column search_vector tsvector
        generated always as (
            to_tsvector('russian', coalesce(first_name, '') || ' ' ||
                                   coalesce(last_name, '') || ' ' ||
                                   coalesce(nickname, '') || ' ' ||
                                   coalesce(description, ''))
            ) stored;

create index artist_search_vector_idx
    on artist
        using GIN (search_vector);

create sequence event_id
    start with 100000
    increment by 1;

CREATE TABLE event
(
    id          bigint  default nextval('event_id'),
    name        varchar(255) not null,
    category    varchar(255) not null,
    location_id varchar(2048),
    hall_id     varchar(2048),
    description text,
    start_time  timestamp,
    end_time    timestamp,
    canceled    boolean default false,
    video_key   varchar(2048),
    popularity  int     DEFAULT 0,
    deleted     boolean default false,
    creator_id  bigint       not null,
    ------------------------------------------
    constraint event_id_pk primary key (id)
);

comment
    on column event.id is 'ID события';
comment
    on column event.name is 'Название события';
comment
    on column event.category is 'Категория события';
comment
    on column event.location_id is 'ID места события';
comment
    on column event.hall_id is 'ID зала события';
comment
    on column event.description is 'Описание события';
comment
    on column event.start_time is 'Время начала';
comment
    on column event.end_time is 'Время окончания';
comment
    on column event.canceled is 'Проверка на отмену события';
comment
    on column event.video_key is 'Ссылка на видео события';
comment
    on column event.popularity is 'Популярность события';
comment
    on column event.creator_id is 'ID создателя мероприятия (того кто добавил)';

create sequence event_image_id
    start with 100000
    increment by 1;

create table event_image
(
    id       bigint default nextval('event_image_id'),
    event_id bigint,
    key      varchar(2048) not null,
    ---
    constraint event_image_id_pk primary key (id),
    constraint event_image_event_id_fk foreign key (event_id) references event (id)
);

create table event_artist
(
    event_id  bigint,
    artist_id bigint,
    ---
    constraint event_artist_event_id_fk foreign key (event_id) references event (id),
    constraint event_artist_artist_id_fk foreign key (artist_id) references artist (id),
    constraint event_artist_id_pk primary key (event_id, artist_id)
);

create sequence ticket_id
    start with 100000
    increment by 1;

CREATE TABLE ticket
(
    id          bigint default nextval('ticket_id'),
    user_id     bigint,
    event_id    bigint,
    location_id varchar(2048),
    hall_id     varchar(2048),
    row_num     bigint,
    seat_num    bigint,
    category    varchar(255),
    cost        DECIMAL,
    is_sell     boolean default false,
    deleted     boolean default false,
    ---
    constraint ticket_event_id_fk foreign key (event_id) references event (id),
    constraint ticket_id_pk primary key (id)
);

comment
    on column ticket.id is 'ID билета';
comment
    on column ticket.user_id is 'ID пользователя';
comment
    on column ticket.event_id is 'ID события';
comment
    on column ticket.location_id is 'ID места события';
comment
    on column ticket.hall_id is 'ID зала события';
comment
    on column ticket.row_num is 'Место ряда события';
comment
    on column ticket.seat_num is 'Место места события';
comment
    on column ticket.category is 'Категория билета';
comment
    on column ticket.cost is 'Цена события';