
CREATE SCHEMA "auth";

-- ----------------------------
--  Table structure for user_role
-- ----------------------------
CREATE TABLE "auth"."user_role" (
	"user_id" int8 NOT NULL,
	"role_id" int8 NOT NULL
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Table structure for role
-- ----------------------------
CREATE TABLE "auth"."role" (
	"id" bigserial NOT NULL,
	"name" varchar(255),
	"display_name" varchar(255),
	"description" varchar(4096)
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Table structure for user
-- ----------------------------
CREATE TABLE "auth"."user" (
	"id" bigserial NOT NULL,
	"name" varchar(255),
	"password" varchar(255),
	"display_name" varchar(255),
	"email" varchar(255),
	"description" varchar(4096),
	"create_date" timestamp
)
WITH (OIDS=FALSE);

-- ----------------------------
--  View structure for v_user_role
-- ----------------------------
-- DROP VIEW IF EXISTS "auth"."v_user_role";
CREATE VIEW "auth"."v_user_role" AS  SELECT u.name AS user_name,
    g.name AS role_name
   FROM auth.user u,
    auth.role g,
    auth.user_role ug
  WHERE ((ug.user_id = u.id) AND (ug.role_id = g.id));

-- ----------------------------
--  Primary key structure for table user_role
-- ----------------------------
ALTER TABLE "auth"."user_role" ADD PRIMARY KEY ("user_id", "role_id");

-- ----------------------------
--  Primary key structure for table role
-- ----------------------------
ALTER TABLE "auth"."role" ADD PRIMARY KEY ("id");

-- ----------------------------
--  Indexes structure for table role
-- ----------------------------
CREATE UNIQUE INDEX  "idx_role_name" ON "auth"."role" USING btree("name");

-- ----------------------------
--  Primary key structure for table user
-- ----------------------------
ALTER TABLE "auth"."user" ADD PRIMARY KEY ("id");

-- ----------------------------
--  Indexes structure for table user
-- ----------------------------
CREATE UNIQUE INDEX  "idx_user_name" ON "auth"."user" USING btree("name");

-- ----------------------------
--  Foreign keys structure for table user_role
-- ----------------------------
ALTER TABLE "auth"."user_role" ADD CONSTRAINT "user_role_user_id_fkey" FOREIGN KEY ("user_id") REFERENCES "auth"."user" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;
ALTER TABLE "auth"."user_role" ADD CONSTRAINT "user_role_role_id_fkey" FOREIGN KEY ("role_id") REFERENCES "auth"."role" ("id") ON UPDATE NO ACTION ON DELETE NO ACTION;

