CREATE TABLE Person (
    person_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(100),
    age INTEGER,
    has_driving_license BOOLEAN
);


CREATE TABLE Car (
    car_id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    brand VARCHAR(50),
    model VARCHAR(50),
    price DECIMAL(10, 2)
);

CREATE TABLE PersonCar (
    person_id INT REFERENCES Person(person_id) ON DELETE CASCADE,
    car_id INT REFERENCES Car(car_id) ON DELETE CASCADE,
    PRIMARY KEY (person_id, car_id)
);



ALTER TABLE Person
ADD CONSTRAINT age_license_check
CHECK (
    (age < 18 AND has_driving_license = FALSE)
    OR
    (age >= 18)
);

CREATE OR REPLACE FUNCTION check_license_before_insert()
RETURNS TRIGGER AS $$
BEGIN
    IF (EXISTS (SELECT 1 FROM Person WHERE person_id = NEW.person_id AND has_driving_license = TRUE)) THEN
        RETURN NEW;
    ELSE
        RAISE EXCEPTION 'Человеку без прав нельзя назначить машину';
    END IF;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_license
BEFORE INSERT ON PersonCar
FOR EACH ROW
EXECUTE FUNCTION check_license_before_insert();