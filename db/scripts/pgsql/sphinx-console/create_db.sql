-- create database sphinx-console
DROP DATABASE IF EXISTS sphinx-console;

CREATE DATABASE sphinx-console
  WITH OWNER = sphinx-console
       ENCODING = ''UTF8''
       TABLESPACE = pg_default
       LC_COLLATE = ''en_US.UTF-8''
       LC_CTYPE = ''en_US.UTF-8''
       CONNECTION LIMIT = -1;
