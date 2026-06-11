#!/bin/bash
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE USER exampleuser WITH ENCRYPTED PASSWORD 'examplesecret';
    ALTER ROLE exampleuser WITH SUPERUSER;
    CREATE DATABASE example;
    GRANT ALL PRIVILEGES ON DATABASE example TO exampleuser;
EOSQL

psql -v ON_ERROR_STOP=1 --dbname example --username exampleuser -w <<-EOSQL
    create extension "uuid-ossp";
EOSQL
