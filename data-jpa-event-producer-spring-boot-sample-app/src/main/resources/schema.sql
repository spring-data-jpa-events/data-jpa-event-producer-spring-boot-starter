create table if not exists "organization" (
    id UUID primary key,
    name varchar not null,
    email varchar not null unique
);

create table if not exists "user" (
    id UUID primary key,
    name varchar not null,
    email varchar not null unique,
    organization_id UUID references organization not null
);