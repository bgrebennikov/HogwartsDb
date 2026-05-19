create table car
(
    id    serial primary key,
    brand varchar(100)   not null,
    model varchar(100)   not null,
    price numeric(12, 2) not null
);

create table person(
    id serial primary key ,
    name varchar(255) not null ,
    age int not null ,
    has_driver_license boolean not null default false,
    car_id int references car (id) on delete set null
)