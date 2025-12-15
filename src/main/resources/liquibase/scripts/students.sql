-- liquibase formatted sql
-- changeset mvasechko:1
create index student_name_index on student (name);