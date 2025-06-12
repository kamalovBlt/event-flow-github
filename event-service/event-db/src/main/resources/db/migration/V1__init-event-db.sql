create sequence artist_id
    start with 100000
    increment by 1;

create table artist
(
    id          bigint  default nextval('artist_id'),
    first_name  varchar(255) not null,
    last_name   varchar(255) not null,
    nickname    varchar(255) not null,
    description text,
    deleted     boolean default false,
    ---
    constraint artist_id_pk primary key (id)
);

comment
    on column artist.id is 'ID';
comment
    on column artist.first_name is 'Имя артиста';
comment
    on column artist.last_name is 'Фамилия артиста';
comment
    on column artist.nickname is 'Псевдоним артиста';
comment
    on column artist.description is 'Описание артиста';


create sequence event_category_id
    start with 100000
    increment by 1;

create table event_category
(
    id      bigint  default nextval('event_category_id'),
    name    varchar(255) not null,
    deleted boolean default false,
    ---
    constraint event_category_id_pk primary key (id),
    constraint event_category_name_unique unique (name)
);

create sequence event_id
    start with 100000
    increment by 1;

CREATE TABLE event
(
    id          bigint  default nextval('event_id'),
    name        varchar(255) not null,
    category_id bigint          not null,
    location_id varchar(255), -- from Mongo (location Service)
    hall_id     bigint,
    description text,
    date        timestamp,
    canceled    boolean default false,
    video_key   varchar(2048),
    popularity  int     DEFAULT 0,
    deleted     boolean default false,
    ---
    constraint event_id_pk primary key (id),
    constraint event_category_id_fk foreign key (category_id) references event_category (id)
);

comment
    on column event.id is 'ID';
comment
    on column event.name is 'Название события';
comment
    on column event.category_id is 'ID категории события';
comment
    on column event.location_id is 'ID места события';
comment
    on column event.hall_id is 'ID зала события';
comment
    on column event.description is 'Описание события';
comment
    on column event.date is 'Дата события';
comment
    on column event.canceled is 'Проверка на отмену события';
comment
    on column event.video_key is 'Ссылка на видео события';
comment
    on column event.popularity is 'Популярность события';

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

create table event_organizer
(
    event_id     bigint,
    organizer_id bigint, -- from User Service,
    ---
    constraint event_organizer_event_id_fk foreign key (event_id) references event (id),
    constraint event_organizer_id_pk primary key (event_id, organizer_id)
);

CREATE TABLE ticket
(
    user_id     bigint, -- from User Service
    event_id    bigint, -- from this service
    location_id varchar(255), -- from location Service
    hall_id     bigint,
    row_id      bigint,
    seat_id     bigint,
    category_id bigint,
    cost        DECIMAL,
    deleted     boolean default false,
    ---
    constraint ticket_event_id_fk foreign key (event_id) references event (id),
    constraint ticket_pk primary key (user_id, event_id, location_id, hall_id, row_id, seat_id),
    constraint ticket_category_fk foreign key (category_id) references event_category (id)
);
comment
    on column ticket.user_id is 'ID пользователя';
comment
    on column ticket.event_id is 'ID события';
comment
    on column ticket.location_id is 'ID места события';
comment
    on column ticket.hall_id is 'ID зала события';
comment
    on column ticket.row_id is 'ID ряда события';
comment
    on column ticket.seat_id is 'ID места события';
comment
    on column ticket.category_id is 'ID категории события';
comment
    on column ticket.cost is 'Цена события';