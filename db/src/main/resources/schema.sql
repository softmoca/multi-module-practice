-- The schema belongs to the db module, not to application.
-- DATE and TIME are reserved words in H2 2.x, hence the prefixed columns.
create table if not exists reservation
(
    id               bigint auto_increment primary key,
    name             varchar(50) not null,
    reservation_date date        not null,
    reservation_time time        not null
);
