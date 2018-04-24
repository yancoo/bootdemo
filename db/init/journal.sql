
CREATE SCHEMA "journal";

create table "journal"."journal"(
    "id" bigserial NOT NULL,
    "menu" varchar(128),
    "sub_menu" varchar(128),
    "type" varchar(128) NOT NULL,
    "sub_type" varchar(128),
    "obj_type" varchar(128),
    "user_name" varchar(255),
    "user_ip" varchar(128),
    "session_id" varchar(128),
    "content" varchar(40960),
    "create_date" timestamp
);
ALTER TABLE "journal"."journal" ADD PRIMARY KEY ("id");