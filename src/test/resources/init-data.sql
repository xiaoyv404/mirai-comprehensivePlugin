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

drop table if exists "MinecraftServerPlayers";
CREATE TABLE "MinecraftServerPlayers"
(
    "id"              varchar(255) COLLATE "pg_catalog"."default" NOT NULL,
    "name"            varchar(255) COLLATE "pg_catalog"."default",
    "lastLoginTime"   timestamp(6),
    "lastLoginServer" varchar(255) COLLATE "pg_catalog"."default",
    "permissions"     varchar(40) COLLATE "pg_catalog"."default"
);

drop table if exists "MinecraftServerPlayer_qq_mapping";
CREATE TABLE "MinecraftServerPlayer_qq_mapping"
(
    "qq"         int8         NOT NULL,
    "playerName" varchar(255) NOT NULL COLLATE "pg_catalog"."default",
    "lock"       bool         NOT NULL
);

drop table if exists "UserAlertLogs";
create table "UserAlertLogs"
(
    "target"   int8 not null,
    "executor" int8 not null,
    "time"     timestamp(6),
    "type"     varchar(255)
);

drop table if exists "MinecraftServer";
create table "MinecraftServer"
(
    "id"           int8 not null,
    "host"         varchar(255),
    "port"         int8,
    "status"       varchar(9),
    "name"         varchar(255),
    "playerNum"    int8,
    "playerMaxNum" int8,
    "hilde"        boolean,
    "mock"         boolean
);

insert into "Groups" ("id", "notice", "permission", "salutatory", "type")
values (1147939635, null, null, null, 'MCG'),
       (2020, null,
        '{
          "MinecraftServerPlayerPermission": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "DebuMe": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "NetworkEro": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "LocalGallery": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "ThesaurusAdd": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "SauceNaoSearch": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "BiliBiliParsing": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          },
          "ThesaurusResponse": {
            "all": true,
            "black": [],
            "white": [],
            "controller": {
              "white": [],
              "groupAdmin": true
            }
          }
        }'
           , null, 'MCG');

INSERT INTO "Users"("id", "admin", "bot", "warning_times")
values (123, true, false, 0),
       (1, false, false, 2);

INSERT INTO "MinecraftServerPlayers"("id", "name", "lastLoginTime", "lastLoginServer", "permissions")
values ('test', 'Test', '2006-04-16 06:58:39.810', 'Test', 'OP'),
       ('test2', 'Test2', '2006-04-16 06:58:39.810', 'Test', 'Default'),
       ('404', '404', '2005-04-16 06:58:39.810', 'Test', 'Default'),
       ('2429334909', '2429334909', '2006-04-16 06:58:39.810', 'Test', 'Default');

INSERT INTO "MinecraftServerPlayer_qq_mapping"("qq", "playerName", "lock")
values (2021, 'Test', true);

INSERT INTO "UserAlertLogs"("target", "executor", "time", "type")
values (1, 2, '2006-04-16 06:58:39.810', 'Increase'),
       (1, 2, '2006-04-16 06:58:39.810', 'Increase');

INSERT INTO "MinecraftServer"("id", "host", "port", "status", "name", "playerNum", "playerMaxNum", "hilde", "mock")
values (1, 'test', 1, 'Online', 'mcg', 10, 80, false, false);
