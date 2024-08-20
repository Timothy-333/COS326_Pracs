CREATE TYPE categoryEnum AS ENUM ('Part-Time', 'Full-Time');

CREATE DOMAIN courseCodeDomain AS VARCHAR(6)
    CHECK (VALUE ~ '^[A-Za-z]{3}[0-9]{3}$');

CREATE TYPE titleEnum AS ENUM ('Ms', 'Mev', 'Miss', 'Mrs', 'Mr', 'Mnr', 'Dr', 'Prof');

CREATE SEQUENCE student_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE degree_key_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE SEQUENCE course_key_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;

CREATE TABLE student (
    student_id INTEGER PRIMARY KEY DEFAULT nextval('student_id_seq'),
    studentNumber CHAR(6),
    title titleEnum,
    fullName VARCHAR(100),
    lastName VARCHAR(100),
    DOB DATE,
    degreeCode VARCHAR(10),
    yearsOfStudy INTEGER
);

CREATE TABLE undergraduate (
    courseRegistration courseCodeDomain[],
    PRIMARY KEY (student_id)
) INHERITS (student);

CREATE TABLE postgraduate (
    category categoryEnum,
    supervisor VARCHAR(100) NOT NULL,
    PRIMARY KEY (student_id)
) INHERITS (student);

CREATE TABLE degreeProgram (
    degreeKey INTEGER PRIMARY KEY DEFAULT nextval('degree_key_seq'),
    degreeCode VARCHAR(10) NOT NULL,
    degreeName VARCHAR(100) NOT NULL,
    numberOfYears INTEGER NOT NULL,
    faculty VARCHAR(100) NOT NULL
);

CREATE TABLE course (
    courseKey INTEGER PRIMARY KEY DEFAULT nextval('course_key_seq'),
    courseCode courseCodeDomain NOT NULL,
    courseName VARCHAR(100) NOT NULL,
    department VARCHAR(100) NOT NULL,
    credits INTEGER NOT NULL
);

CREATE FUNCTION personFullNames(studentNumber CHAR(6))
RETURNS TEXT AS $$
    SELECT title || ' ' || fullName || ' ' || lastName
    FROM student
    WHERE studentNumber = $1;
$$ LANGUAGE SQL;

CREATE FUNCTION ageInYears(studentNumber CHAR(6))
RETURNS INTEGER AS $$
    SELECT EXTRACT(YEAR FROM AGE(DOB))
    FROM student
    WHERE studentNumber = $1;
$$ LANGUAGE SQL;

CREATE FUNCTION isRegisteredFor(student_id INTEGER, courseCode courseCodeDomain)
RETURNS BOOLEAN AS $$
    SELECT EXISTS (
        SELECT 1
        FROM undergraduate
        WHERE student_id = $1 AND courseCode = ANY(courseRegistration)
    );
$$ LANGUAGE SQL;

CREATE FUNCTION isFinalYearStudent(student_id INTEGER)
RETURNS BOOLEAN AS $$
    SELECT (yearsOfStudy >= numberOfYears)
    FROM undergraduate u
    JOIN degreeProgram d ON u.degreecode = d.degreecode
    WHERE u.student_id = $1;
$$ LANGUAGE SQL;


CREATE FUNCTION isFullTime(student_id INTEGER)
RETURNS BOOLEAN AS $$
    SELECT CASE
        WHEN category = 'Full-Time' THEN TRUE
        ELSE FALSE
    END
    FROM postgraduate
    WHERE student_id = $1;
$$ LANGUAGE SQL;


CREATE FUNCTION isPartTime(student_id INTEGER)
RETURNS BOOLEAN AS $$
    SELECT CASE
        WHEN category = 'Part-Time' THEN TRUE
        ELSE FALSE
    END
    FROM postgraduate
    WHERE student_id = $1;
$$ LANGUAGE SQL;
