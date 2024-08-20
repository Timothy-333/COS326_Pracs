--1
SELECT 
    student_id,
    studentNumber,
    personFullNames(studentNumber) AS personFullName,
    ageInYears(studentNumber) AS ageInYears
FROM 
    student;
--2
SELECT 
    student_id,
    studentNumber,
    personFullNames(studentNumber) AS personFullName,
    degreecode AS studentDegreeCode,
    yearsOfStudy AS studentYearOfStudy,
    courseRegistration
FROM 
    undergraduate;
--3
SELECT 
    student_id,
    studentNumber,
    personFullNames(studentNumber) AS personFullName,
    degreecode AS studentDegreeCode,
    yearsOfStudy AS studentYearOfStudy,
    category AS postgraduateCategory,
    supervisor AS supervisorName
FROM 
    postgraduate;
--4
SELECT 
    student_id,
    personFullNames(studentNumber) AS personFullName,
    degreecode AS studentDegreeCode,
    yearsOfStudy AS studentYearOfStudy,
    courseRegistration
FROM 
    undergraduate
WHERE 
    isFinalYearStudent(student_id);
--5
SELECT 
    student_id,
    studentNumber,
    personFullNames(studentNumber) AS personFullName,
    degreecode AS studentDegreeCode,
    yearsOfStudy AS studentYearOfStudy,
    courseRegistration
FROM 
    undergraduate
WHERE 
    isRegisteredFor(student_id, 'COS326');
--6
SELECT 
    student_id,
    studentNumber,
    personFullNames(studentNumber) AS personFullName,
    degreecode AS studentDegreeCode,
    yearsOfStudy AS studentYearOfStudy,
    category AS postgraduateCategory,
    supervisor AS supervisorName
FROM 
    postgraduate
WHERE 
    isFullTime(student_id);
--7
SELECT 
    student_id,
    studentNumber,
    personFullNames(studentNumber) AS personFullName,
    degreecode AS studentDegreeCode,
    yearsOfStudy AS studentYearOfStudy,
    category AS postgraduateCategory,
    supervisor AS supervisorName
FROM 
    postgraduate
WHERE 
    isPartTime(student_id);
