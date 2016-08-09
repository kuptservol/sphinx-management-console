-- execute from user sphinx-console in db sphinx-console
-- SHEMA test
DROP SCHEMA IF EXISTS test CASCADE;

CREATE SCHEMA test
  AUTHORIZATION sphinx-console;

set schema 'test';

-- TABLE files
DROP TABLE IF EXISTS files cascade;
CREATE SEQUENCE files_id_seq;
CREATE TABLE files
(
  id integer PRIMARY KEY default nextval('files_id_seq'),
  text_data character varying(4000) DEFAULT NULL::character varying,
  name character varying(100) DEFAULT NULL::character varying,
  text2 character varying(4000) DEFAULT NULL::character varying,
  value character varying(15) DEFAULT NULL::character varying,
  is_deleted boolean NOT NULL DEFAULT false,
  deleted boolean NOT NULL DEFAULT false,
  snippet_text character varying(100) DEFAULT NULL::character varying,
  snippet_pre_query boolean NOT NULL DEFAULT false,
  snippet_post_query boolean NOT NULL DEFAULT false
)
WITH (
  OIDS=FALSE
);
ALTER TABLE files
  OWNER TO sphinx-console;

alter sequence files_id_seq owned by files.id;  

-- TABLE MAIN
DROP TABLE IF EXISTS main cascade;
CREATE SEQUENCE main_id_seq;
CREATE TABLE main
(
  id integer PRIMARY KEY default nextval('main_id_seq'),
  value character varying(300) DEFAULT NULL::character varying,
  value2 character varying(300) DEFAULT NULL::character varying,
  create_date timestamp default CURRENT_TIMESTAMP,
  is_deleted boolean NOT NULL DEFAULT false
)
WITH (
  OIDS=FALSE
);
ALTER TABLE main
  OWNER TO sphinx-console;

alter sequence main_id_seq owned by main.id;  
insert into main (id, value, is_deleted) values (1, 2, false);
insert into main (id, value, is_deleted) values (2, 'for test --merge-dst-range', true);

-- TABLE DELTA
DROP TABLE IF EXISTS delta cascade;
CREATE SEQUENCE delta_id_seq MINVALUE 2;
CREATE TABLE delta
(
  id integer PRIMARY KEY default nextval('delta_id_seq'),
  value character varying(300) DEFAULT NULL::character varying,
  value2 character varying(300) DEFAULT NULL::character varying,
  create_date timestamp default CURRENT_TIMESTAMP,
  is_deleted boolean NOT NULL DEFAULT false
)
WITH (
  OIDS=FALSE
);
ALTER TABLE delta
  OWNER TO sphinx-console;

alter sequence delta_id_seq owned by delta.id;  

-- TABLE MERGE_DATE
DROP TABLE IF EXISTS merge_date cascade;
CREATE TABLE merge_date
(
  merge_timestamp timestamp with time zone NOT NULL DEFAULT now()
)
WITH (
  OIDS=FALSE
);
ALTER TABLE merge_date
  OWNER TO sphinx-console;

-------------------
CREATE OR REPLACE FUNCTION from_unixtime(integer) RETURNS timestamp AS 'SELECT $1::abstime::timestamp without time zone AS result' LANGUAGE SQL;
ALTER TABLE test.files ADD COLUMN last_changed_date DATE NULL;
