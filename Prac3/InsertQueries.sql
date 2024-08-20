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
INSERT INTO undergraduate (studentNumber, title, fullName, lastName, DOB, degreeCode, yearsOfStudy, courseRegistration)
VALUES 
    ('140010', 'Ms', 'Emily', 'Johnson', '1996-01-10', 'BSC', 3, ARRAY['COS301', 'COS326', 'MTH301']),
    ('140015', 'Mr', 'David', 'Smith', '1995-05-25', 'BSC', 3, ARRAY['COS301', 'PHL301', 'MTH301']),
    ('131120', 'Ms', 'Sophia', 'Williams', '1995-01-30', 'BIT', 3, ARRAY['COS301', 'COS326', 'PHL301']),
    ('131140', 'Mr', 'James', 'Brown', '1996-02-20', 'BIT', 4, ARRAY['COS301', 'COS326', 'MTH301', 'PHL301']);
INSERT INTO postgraduate (studentNumber, title, fullName, lastName, DOB, degreeCode, yearsOfStudy, category, supervisor)
VALUES 
    ('101122', 'Mrs', 'Olivia', 'Taylor', '1987-06-15', 'PHD', 2, 'Full-Time', 'Mr. You Dunno'),
    ('121101', 'Mrs', 'Ava', 'Martinez', '1985-04-27', 'PHD', 3, 'Part-Time', 'Mrs. You Doknow');
