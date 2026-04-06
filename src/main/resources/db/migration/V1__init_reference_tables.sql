-- V1__init_reference_tables.sql

CREATE TABLE industries (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE organization_types (
    id UUID PRIMARY KEY,
    name VARCHAR(255) UNIQUE NOT NULL
);