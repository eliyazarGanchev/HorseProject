-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE FROM horse where id < 0;
DELETE FROM owner WHERE id < 0;

INSERT INTO owner (id, first_name, last_name, description)
VALUES (-1, 'Alice', 'Smith', 'I am Alice');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-2, 'Henry', 'Bieber', 'I am Henry');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-3, 'Charlie', 'Holland', 'I am Charlie');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-4, 'Daniel', 'David', 'I am Daniel');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-5, 'Eva', 'Adams', 'I am Eva');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-6, 'Clara', 'Brown', 'I am Clara');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-7, 'Grace', 'Jolie', 'I am Grace');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-8, 'Henry', 'Johnson', 'I am Henry');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-9, 'Boris', 'Beckham', 'I am Boris');
INSERT INTO owner (id, first_name, last_name, description)
VALUES (-10, 'Jax', 'Davis', 'I am Jax');

INSERT INTO horse (id, name, description, date_of_birth, sex)
VALUES (-1, 'Wendy', 'The famous one!', '2012-12-12', 'FEMALE');

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-2, 'Max', 'A strong stallion', '2010-05-15', 'MALE', -2, NULL, NULL, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-3, 'Bella', 'Child of Wendy and Max', '2015-06-20', 'FEMALE', -3, -1, -2, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-4, 'Daisy', 'Child of Wendy and Max', '2016-08-01', 'FEMALE', -4, -1, -2, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-5, 'Jason', 'Child of Bella, grandchild of Wendy and Max', '2018-07-10', 'MALE', -5, -3, NULL, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-6, 'Sam', 'Sam', '2011-04-04', 'MALE', -6, NULL, NULL, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-7, 'Jenny', 'Child of Bella and Sam', '2018-09-09', 'FEMALE', -7, -3, -6, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-8, 'Leo', 'Child of Jenny', '2020-01-15', 'MALE', -8, -7, NULL, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-9, 'Mia', 'Child of Daisy and Max', '2019-03-10', 'FEMALE', -9, -4, -2, NULL, NULL);

INSERT INTO horse (id, name, description, date_of_birth, sex, owner_id, mother_id, father_id, image, image_type)
VALUES (-10, 'Bob', 'Child of Mia and Leo', '2022-05-20', 'MALE', -10, -9, -8, NULL, NULL);
