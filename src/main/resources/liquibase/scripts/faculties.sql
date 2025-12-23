-- liquibase formatted sql
-- changeset mvasechko:2
create index faculty_name_color_index on faculty (name, color);