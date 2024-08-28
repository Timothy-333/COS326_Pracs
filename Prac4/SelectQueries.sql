
--1
SELECT 
    s.studentNumber, 
    personFullNames(s.name) AS fullNames, 
    ageInYears(s.DOB) AS age, 
    s.degreeCode AS degreeProgram
FROM 
    student s
WHERE 
    isRegisteredFor(s.studentNumber, ARRAY['COS326'::courseCodeDomain]);

--2 Assuming the course codes 'COS326' and 'COS301' are valid
SELECT 
    studentNumber, 
    hasValidCourseCodes(ARRAY['COS301'::courseCodeDomain, 'COS326'::courseCodeDomain, 'MTH301'::courseCodeDomain]) AS validCourses
FROM 
    undergraduate
WHERE 
    studentNumber = '140010';

--3 Assuming the course code 'INVALID' is not valid
SELECT 
    studentNumber, 
    hasValidCourseCodes(ARRAY['COS301'::courseCodeDomain, 'COS999'::courseCodeDomain, 'MTH301'::courseCodeDomain]) AS validCourses
FROM 
    undergraduate
WHERE 
    studentNumber = '140015';

--4 Assuming the student has no duplicate course codes
SELECT 
    studentNumber, 
    hasDuplicateCourseCodes(ARRAY['COS301'::courseCodeDomain, 'COS326'::courseCodeDomain, 'MTH301'::courseCodeDomain]) AS hasDuplicates
FROM 
    undergraduate
WHERE 
    studentNumber = '140010';

--5 Assuming the student has duplicate course codes
SELECT 
    studentNumber, 
    hasDuplicateCourseCodes(ARRAY['COS301'::courseCodeDomain, 'COS301'::courseCodeDomain, 'MTH301'::courseCodeDomain]) AS hasDuplicates
FROM 
    undergraduate
WHERE 
    studentNumber = '140015';
