
SET client_encoding = 'UTF8'; 

-- ----------------------------
--  Records of user
-- ----------------------------
BEGIN;
INSERT INTO "auth"."user" VALUES ('1', 'dev', '34c6fceca75e456f25e7e99531e2425c6c1de443', '孜孜不倦', null, null, now());
INSERT INTO "auth"."user" VALUES ('2', 'admin', 'd033e22ae348aeb5660fc2140aec35850c4da997', '管理员', null, null, now());
INSERT INTO "auth"."user" VALUES ('3', 'user', '12dea96fec20593566ab75692c9949596833adc9', '一般操作员', null, null, now());
COMMIT;

select pg_catalog.setval('auth.user_id_seq', max(id), true) from "auth"."user";

-- ----------------------------
--  Records of role
-- ----------------------------
BEGIN;
INSERT INTO "auth"."role" VALUES ('1', 'DEV', '孜孜不倦', '');
INSERT INTO "auth"."role" VALUES ('2', 'ADMIN', '系统管理员', '');
INSERT INTO "auth"."role" VALUES ('3', 'USER', '操作员', '');
COMMIT;

select pg_catalog.setval('auth.role_id_seq', max(id), true) from "auth"."role";

-- ----------------------------
--  Records of user_role
-- ----------------------------
BEGIN;
INSERT INTO "auth"."user_role" VALUES ('1', '1');
INSERT INTO "auth"."user_role" VALUES ('1', '2');
INSERT INTO "auth"."user_role" VALUES ('1', '3');
INSERT INTO "auth"."user_role" VALUES ('2', '2');
INSERT INTO "auth"."user_role" VALUES ('2', '3');
INSERT INTO "auth"."user_role" VALUES ('3', '3');
COMMIT;

