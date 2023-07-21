drop table if exists "Groups";
CREATE TABLE "Groups"
(
    "id"         int4 not null,
    "notice"     json,
    "permission" json,
    "salutatory" json,
    "type"       varchar(255)
);
alter table "Groups"
    add constraint "Groups_pkey" primary key ("id");

drop table if exists "Users";
CREATE TABLE "Users"
(
    "id"            int8 not null,
    "admin"         bool,
    "bot"           bool,
    "warning_times" int2
);
alter table "Users"
    add constraint "Users_pkey" primary key ("id");

insert into "Groups" ("id", "notice", "permission", "salutatory", "type")
values (1147939635, null, null, null, 'MCG'),
       (2020, null, null, null, 'MCG');