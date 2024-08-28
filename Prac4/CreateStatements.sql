CREATE TYPE categoryEnum AS ENUM ('Part_Time', 'Full_Time');

CREATE DOMAIN courseCodeDomain AS VARCHAR(6)
    CHECK (VALUE ~ '^[A-Za-z]{3}[0-9]{3}$');

CREATE DOMAIN StudyYearsType AS INTEGER
    CHECK (VALUE >= 0 AND VALUE <= 99);

CREATE TYPE titleEnum AS ENUM ('Ms', 'Mev', 'Miss', 'Mrs', 'Mr', 'Mnr', 'Dr', 'Prof');

CREATE TYPE NameType AS (
    title titleEnum,
    firstName VARCHAR(100),
    surname VARCHAR(100)
);


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
    name NameType,
    DOB DATE,
    degreeCode VARCHAR(10),
    yearsOfStudy StudyYearsType
);

CREATE TABLE undergraduate (
    courseRegistration courseCodeDomain[],
    PRIMARY KEY (student_id)
) INHERITS (student);

CREATE TABLE postgraduate (
    category categoryEnum,
    supervisor NameType,
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

CREATE TABLE DeletedUndergrad (
    deletionTimestamp TIMESTAMP NOT NULL,
    deletedByUserId VARCHAR(50) NOT NULL,
    PRIMARY KEY (student_id)
) INHERITS (undergraduate);

CREATE TABLE DeletedPostgrad (
    deletionTimestamp TIMESTAMP NOT NULL,
    deletedByUserId VARCHAR(50) NOT NULL,
    PRIMARY KEY (student_id)
) INHERITS (postgraduate);

-- Function: personFullNames(NameType)
CREATE FUNCTION personFullNames(name NameType)
RETURNS TEXT AS $$
BEGIN
    RETURN (name.title || ' ' || name.firstName || ' ' || name.surname);
END;
$$ LANGUAGE plpgsql;

-- Function: ageInYears(DATE)
CREATE FUNCTION ageInYears(DOB DATE)
RETURNS INTEGER AS $$
BEGIN
    RETURN EXTRACT(YEAR FROM AGE(DOB));
END;
$$ LANGUAGE plpgsql;

CREATE FUNCTION isRegisteredFor(p_studentNumber CHAR(6), courseCodes courseCodeDomain[])
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM undergraduate
        WHERE studentNumber = p_studentNumber AND courseCodes && courseRegistration
    );
END;
$$ LANGUAGE plpgsql;

-- Function: isValidCourseCode(courseCodeDomain)
CREATE FUNCTION isValidCourseCode(p_courseCode courseCodeDomain)
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM Course c
        WHERE c.courseCode = p_courseCode
    );
END;
$$ LANGUAGE plpgsql;

-- Function: hasValidCourseCodes()
CREATE FUNCTION hasValidCourseCodes(p_courseRegistration courseCodeDomain[])
RETURNS BOOLEAN AS $$
DECLARE
    p_courseCode courseCodeDomain;
BEGIN
    FOREACH p_courseCode IN ARRAY p_courseRegistration
    LOOP
        IF NOT isValidCourseCode(p_courseCode) THEN
            RETURN FALSE;
        END IF;
    END LOOP;
    RETURN TRUE;
END;
$$ LANGUAGE plpgsql;



-- Function: courseCodeFrequency(courseCodeDomain, courseCodeDomain[])
CREATE FUNCTION courseCodeFrequency(courseCode courseCodeDomain, courseRegistration courseCodeDomain[])
RETURNS INTEGER AS $$
DECLARE
    count INTEGER := 0;
    code courseCodeDomain;
BEGIN
    FOREACH code IN ARRAY courseRegistration
    LOOP
        IF code = courseCode THEN
            count := count + 1;
        END IF;
    END LOOP;
    RETURN count;
END;
$$ LANGUAGE plpgsql;

-- Function: hasDuplicateCourseCodes()
CREATE FUNCTION hasDuplicateCourseCodes(courseRegistration courseCodeDomain[])
RETURNS BOOLEAN AS $$
DECLARE
    courseCode courseCodeDomain;
    count INTEGER;
BEGIN
    FOREACH courseCode IN ARRAY courseRegistration
    LOOP
        count := courseCodeFrequency(courseCode, courseRegistration);
        IF count > 1 THEN
            RETURN TRUE;
        END IF;
    END LOOP;
    RETURN FALSE;
END;
$$ LANGUAGE plpgsql;

-- Function: isValidDegreeCode(TEXT)
CREATE FUNCTION isValidDegreeCode(p_degreeCode VARCHAR(10))
RETURNS BOOLEAN AS $$
BEGIN
    RETURN EXISTS (
        SELECT 1
        FROM DegreeProgram d
        WHERE d.degreeCode = p_degreeCode
    );
END;
$$ LANGUAGE plpgsql;

-- Trigger Procedure: check_valid_degree_code()
CREATE FUNCTION check_valid_degree_code()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT isValidDegreeCode(NEW.degreeCode) THEN
        RAISE EXCEPTION 'Invalid degree code';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger Procedure: check_valid_course_codes()
CREATE FUNCTION check_valid_course_codes()
RETURNS TRIGGER AS $$
BEGIN
    IF NOT hasValidCourseCodes(NEW.courseRegistration) THEN
        RAISE EXCEPTION 'Invalid course codes';
    END IF;
    IF hasDuplicateCourseCodes(NEW.courseRegistration) THEN
        RAISE EXCEPTION 'Duplicate course codes';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Trigger Procedure: record_delete_undergrad()
CREATE FUNCTION record_delete_undergrad()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO DeletedUndergrad
    SELECT OLD.*, CURRENT_TIMESTAMP, current_user;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger Procedure: record_delete_postgrad()
CREATE FUNCTION record_delete_postgrad()
RETURNS TRIGGER AS $$
BEGIN
    INSERT INTO DeletedPostgrad
    SELECT OLD.*, CURRENT_TIMESTAMP, current_user;
    RETURN OLD;
END;
$$ LANGUAGE plpgsql;

-- Trigger: check_valid_degree
CREATE TRIGGER student_check_valid_degree
BEFORE INSERT OR UPDATE ON Student
FOR EACH ROW EXECUTE FUNCTION check_valid_degree_code();

-- Trigger: check_valid_degree
CREATE TRIGGER undergraduate_check_valid_degree
BEFORE INSERT OR UPDATE ON undergraduate
FOR EACH ROW EXECUTE FUNCTION check_valid_degree_code();

-- Trigger: check_valid_degree
CREATE TRIGGER postgraduate_check_valid_degree
BEFORE INSERT OR UPDATE ON postgraduate
FOR EACH ROW EXECUTE FUNCTION check_valid_degree_code();

-- Trigger: check_valid_course_registration
CREATE TRIGGER check_valid_course_registration
BEFORE INSERT OR UPDATE ON Undergraduate
FOR EACH ROW EXECUTE FUNCTION check_valid_course_codes();

-- Trigger: audit_delete_undergrad
CREATE TRIGGER audit_delete_undergrad
AFTER DELETE ON Undergraduate
FOR EACH ROW EXECUTE FUNCTION record_delete_undergrad();

-- Trigger: audit_delete_postgrad
CREATE TRIGGER audit_delete_postgrad
AFTER DELETE ON Postgraduate
FOR EACH ROW EXECUTE FUNCTION record_delete_postgrad();

