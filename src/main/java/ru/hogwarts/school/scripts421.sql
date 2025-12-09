UPDATE Student SET age = 16 WHERE age < 16;
ALTER TABLE Student
ADD CONSTRAINT age_check CHECK (age >= 16);


ALTER TABLE Student ALTER COLUMN name SET NOT NULL;
CREATE UNIQUE INDEX idx_unique_name ON Student(name);


SELECT name, color, COUNT(*)
FROM Faculty
GROUP BY name, color
HAVING COUNT(*) > 1;

SELECT conname FROM pg_constraint WHERE conname = 'faculty_name_color_unique';
ALTER TABLE Faculty DROP CONSTRAINT IF EXISTS faculty_name_color_unique;

ALTER TABLE Faculty
ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);


ALTER TABLE Student
ALTER COLUMN age SET DEFAULT 20;