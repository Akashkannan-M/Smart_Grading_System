-- Sample SQL to insert test users with BCrypt encoded passwords
-- Password for all is 'password123' (encoded: $2a$10$wSln6GvR6C.UjP2v0Jp0O.a7K2M0/V0wX6FzUq8R./f6v.uR6.K6.)

-- HOD
INSERT INTO users (name, role, department, emp_id, password) 
VALUES ('Test HOD', 'HOD', 'CSE', 'HOD123', '$2a$10$wSln6GvR6C.UjP2v0Jp0O.a7K2M0/V0wX6FzUq8R./f6v.uR6.K6.')
ON CONFLICT (emp_id) DO NOTHING;

-- Staff
INSERT INTO users (name, role, department, emp_id, password) 
VALUES ('Test Staff', 'STAFF', 'CSE', 'STF123', '$2a$10$wSln6GvR6C.UjP2v0Jp0O.a7K2M0/V0wX6FzUq8R./f6v.uR6.K6.')
ON CONFLICT (emp_id) DO NOTHING;

-- Student
INSERT INTO users (name, role, department, reg_no, password) 
VALUES ('Test Student', 'STUDENT', 'CSE', '81440001', '$2a$10$wSln6GvR6C.UjP2v0Jp0O.a7K2M0/V0wX6FzUq8R./f6v.uR6.K6.')
ON CONFLICT (reg_no) DO NOTHING;
