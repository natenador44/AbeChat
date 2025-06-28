create table users (
    id varchar(36) primary key,  -- UUID
    name varchar(128) not null unique,
    password varchar(256) not null -- password hash base64 encoded
);