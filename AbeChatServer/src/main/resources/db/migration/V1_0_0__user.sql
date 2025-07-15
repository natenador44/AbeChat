create table users (
    -- having the primary key be a UUID instead of the name allows users to change their usernames while keeping all other
    -- information the same
    id varchar(36) primary key,  -- UUID
    name varchar(128) not null unique,
    password varchar(256) not null -- password hash base64 encoded
);