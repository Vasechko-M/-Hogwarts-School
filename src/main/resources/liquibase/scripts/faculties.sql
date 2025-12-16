-- liquibase formatted sql
-- changeset mvasechko:2
create index faculty_name_index on faculty (name);
-- changeset mvasechko:3
create index faculty_color_index on faculty (color);