INSERT INTO degreeProgram (degreeCode, degreeName, numberOfYears, faculty)
VALUES 
    ('BSC', 'Bachelor of Science', 3, 'Engineering and Built Environment'),
    ('BIT', 'Bachelor of IT', 4, 'Engineering and Built Environment'),
    ('PHD', 'Doctor of Philosophy', 5, 'Engineering and Built Environment');
INSERT INTO course (courseCode, courseName, department, credits)
VALUES 
    ('COS301', 'Software Engineering', 'Computer Science', 40),
    ('COS326', 'Database Systems', 'Computer Science', 20),
    ('MTH301', 'Discrete Mathematics', 'Mathematics', 15),
    ('PHL301', 'Logical Reasoning', 'Philosophy', 15);
INSERT INTO undergraduate (studentNumber, name, DOB, degreeCode, yearsOfStudy, courseRegistration)
VALUES 
    ('140010', ROW('Ms', 'Emily', 'Johnson')::NameType, '1996-01-10', 'BSC', 3, ARRAY['COS301'::courseCodeDomain, 'COS326'::courseCodeDomain, 'MTH301'::courseCodeDomain]),
    ('140015', ROW('Mr', 'David', 'Smith')::NameType, '1995-05-25', 'BSC', 3, ARRAY['COS301'::courseCodeDomain, 'PHL301'::courseCodeDomain, 'MTH301'::courseCodeDomain]),
    ('131120', ROW('Ms', 'Sophia', 'Williams')::NameType, '1995-01-30', 'BIT', 3, ARRAY['COS301'::courseCodeDomain, 'COS326'::courseCodeDomain, 'PHL301'::courseCodeDomain]),
    ('131140', ROW('Mr', 'James', 'Brown')::NameType, '1996-02-20', 'BIT', 4, ARRAY['COS301'::courseCodeDomain, 'COS326'::courseCodeDomain, 'MTH301'::courseCodeDomain, 'PHL301'::courseCodeDomain]);

INSERT INTO postgraduate (studentNumber, name, DOB, degreeCode, yearsOfStudy, category, supervisor)
VALUES 
    ('101122', ROW('Mrs', 'Olivia', 'Taylor')::NameType, '1987-06-15', 'PHD', 2, 'Full_Time', ROW('Mr', 'You', 'Dunno')::NameType),
    ('121101', ROW('Mrs', 'Ava', 'Martinez')::NameType, '1985-04-27', 'PHD', 3, 'Part_Time', ROW('Mrs', 'You', 'Doknow')::NameType);
