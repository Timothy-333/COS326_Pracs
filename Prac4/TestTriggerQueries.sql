-- Test inserting a record with an invalid degree code
INSERT INTO undergraduate (studentNumber, name, DOB, degreeCode, yearsOfStudy, courseRegistration)
VALUES 
('150001', ROW('Mr', 'Invalid', 'Degree')::NameType, '1999-01-01', 'XYZ', 3, ARRAY['COS301'::courseCodeDomain]);

-- Expected outcome: This should raise an error due to the invalid degree code 'XYZ'.

-- Test inserting a record with an invalid course code
INSERT INTO undergraduate (studentNumber, name, DOB, degreeCode, yearsOfStudy, courseRegistration)
VALUES 
('150002', ROW('Ms', 'Invalid', 'Course')::NameType, '1999-01-01', 'BSC', 3, ARRAY['COS999'::courseCodeDomain]);

-- Expected outcome: This should raise an error due to the invalid course code 'INVALID1'.


-- Test inserting a record with an invalid degree code in postgraduate
INSERT INTO postgraduate (studentNumber, name, DOB, degreeCode, yearsOfStudy, category, supervisor)
VALUES 
('150003', ROW('Mr', 'Invalid', 'Degree')::NameType, '1988-01-01', 'XYZ', 2, 'Full_Time', ROW('Mr', 'You', 'Dunno')::NameType);

-- Expected outcome: This should raise an error due to the invalid degree code 'XYZ'.

-- Test updating a record in undergraduate with an invalid degree code
UPDATE undergraduate
SET degreeCode = 'XYZ'
WHERE studentNumber = '140010';

-- Expected outcome: This should raise an error due to the invalid degree code 'XYZ'.

-- Test updating the courseRegistration array with an invalid course code
UPDATE undergraduate
SET courseRegistration = ARRAY['COS301'::courseCodeDomain, 'COS999'::courseCodeDomain]
WHERE studentNumber = '140010';

-- Expected outcome: This should raise an error due to the invalid course code 'INVALID1'.

-- Test updating a record in postgraduate with an invalid degree code
UPDATE postgraduate
SET degreeCode = 'XYZ'
WHERE studentNumber = '121101';

-- Expected outcome: This should raise an error due to the invalid degree code 'XYZ'.

-- Test deleting a record from undergraduate
DELETE FROM undergraduate
WHERE studentNumber = '140010';

-- Expected outcome: This should trigger the audit_delete_undergrad function and log the deletion.

-- Test deleting a record from postgraduate
DELETE FROM postgraduate
WHERE studentNumber = '121101';

-- Expected outcome: This should trigger the audit_delete_postgrad function and log the deletion.
