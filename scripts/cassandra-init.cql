-- Kreiraj keyspace
CREATE KEYSPACE IF NOT EXISTS nais 
WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 1};

USE nais;

-- Student tabela
CREATE TABLE IF NOT EXISTS students (
    student_id bigint PRIMARY KEY,
    first_name text,
    last_name text,
    index_number text,
    study_program text,
    current_year_of_study int,
    enrollment_year int,
    enrollment_date date,
    is_active boolean
);

-- Subject tabela
CREATE TABLE IF NOT EXISTS subjects (
    subject_id text PRIMARY KEY,
    subject_name text,
    subject_code text,
    department text,
    ects_points int,
    year_of_study int,
    semester int,
    professor_id bigint,
    professor_name text,
    is_mandatory boolean
);

-- Student grades tabela (glavna!)
CREATE TABLE IF NOT EXISTS student_grades (
    student_id bigint,
    academic_year text,
    subject_id text,
    exam_date timestamp,
    grade double,
    exam_type text,
    passed boolean,
    attempt_number int,
    subject_name text,
    subject_code text,
    department text,
    professor_id bigint,
    professor_name text,
    ects_points int,
    PRIMARY KEY (student_id, academic_year, subject_id, exam_date)
);

-- Subject statistics tabela
CREATE TABLE IF NOT EXISTS subject_statistics (
    department text,
    academic_year text,
    subject_id text,
    pass_rate_percentage double,
    average_grade double,
    total_enrolled_students int,
    year_of_study int,
    semester int,
    professor_id bigint,
    PRIMARY KEY (department, academic_year, subject_id)
);

-- Professor performance tabela
CREATE TABLE IF NOT EXISTS professor_performance (
    professor_id bigint,
    academic_year text,
    total_students_taught int,
    total_subjects_taught int,
    total_students_passed int,
    total_students_failed int,
    overall_pass_rate double,
    average_grade_given double,
    total_exams_conducted int,
    hardest_subject_id text,
    hardest_subject_name text,
    hardest_subject_pass_rate double,
    easiest_subject_id text,
    easiest_subject_name text,
    easiest_subject_pass_rate double,
    performance_consistency_score double,
    PRIMARY KEY (professor_id, academic_year)
);

-- Test studenti
INSERT INTO students (student_id, first_name, last_name, index_number, study_program, current_year_of_study, enrollment_year, enrollment_date, is_active) 
VALUES (1, 'Marko', 'Petrovic', 'SW-1-2021', 'Softversko inženjerstvo', 3, 2021, '2021-09-01', true);

INSERT INTO students (student_id, first_name, last_name, index_number, study_program, current_year_of_study, enrollment_year, enrollment_date, is_active) 
VALUES (2, 'Ana', 'Jovanovic', 'SW-2-2021', 'Softversko inženjerstvo', 3, 2021, '2021-09-01', true);

INSERT INTO students (student_id, first_name, last_name, index_number, study_program, current_year_of_study, enrollment_year, enrollment_date, is_active) 
VALUES (3, 'Petar', 'Nikolic', 'SW-3-2021', 'Softversko inženjerstvo', 3, 2021, '2021-09-01', true);

-- Test predmeti
INSERT INTO subjects (subject_id, subject_name, subject_code, department, ects_points, year_of_study, semester, professor_id, professor_name, is_mandatory) 
VALUES ('NAIS', 'Napredni algoritmi i strukture podataka', 'NAIS', 'FTN', 6, 3, 5, 101, 'Prof. Dr Petar Petrovic', true);

INSERT INTO subjects (subject_id, subject_name, subject_code, department, ects_points, year_of_study, semester, professor_id, professor_name, is_mandatory) 
VALUES ('OOP', 'Objektno orijentisano programiranje', 'OOP', 'FTN', 8, 2, 3, 102, 'Prof. Dr Marko Markovic', true);

INSERT INTO subjects (subject_id, subject_name, subject_code, department, ects_points, year_of_study, semester, professor_id, professor_name, is_mandatory) 
VALUES ('WEB', 'Web programiranje', 'WEB', 'FTN', 6, 3, 6, 103, 'Doc. Dr Ana Antic', false);

-- Test ocene
INSERT INTO student_grades (student_id, academic_year, subject_id, exam_date, grade, exam_type, passed, attempt_number, subject_name, department, professor_id) 
VALUES (1, '2024/2025', 'NAIS', '2024-06-15 10:00:00', 8.5, 'REDOVAN', true, 1, 'Napredni algoritmi i strukture podataka', 'FTN', 101);

INSERT INTO student_grades (student_id, academic_year, subject_id, exam_date, grade, exam_type, passed, attempt_number, subject_name, department, professor_id) 
VALUES (2, '2024/2025', 'NAIS', '2024-06-15 10:00:00', 9.0, 'REDOVAN', true, 1, 'Napredni algoritmi i strukture podataka', 'FTN', 101);

INSERT INTO student_grades (student_id, academic_year, subject_id, exam_date, grade, exam_type, passed, attempt_number, subject_name, department, professor_id) 
VALUES (3, '2024/2025', 'NAIS', '2024-06-15 10:00:00', 7.5, 'REDOVAN', true, 1, 'Napredni algoritmi i strukture podataka', 'FTN', 101);

INSERT INTO student_grades (student_id, academic_year, subject_id, exam_date, grade, exam_type, passed, attempt_number, subject_name, department, professor_id) 
VALUES (1, '2024/2025', 'OOP', '2024-01-20 14:00:00', 9.5, 'REDOVAN', true, 1, 'Objektno orijentisano programiranje', 'FTN', 102);

INSERT INTO student_grades (student_id, academic_year, subject_id, exam_date, grade, exam_type, passed, attempt_number, subject_name, department, professor_id) 
VALUES (2, '2024/2025', 'OOP', '2024-01-20 14:00:00', 8.0, 'REDOVAN', true, 1, 'Objektno orijentisano programiranje', 'FTN', 102);

INSERT INTO student_grades (student_id, academic_year, subject_id, exam_date, grade, exam_type, passed, attempt_number, subject_name, department, professor_id) 
VALUES (3, '2024/2025', 'WEB', '2024-09-05 16:00:00', 6.0, 'REDOVAN', true, 1, 'Web programiranje', 'FTN', 103);

-- Test statistike predmeta
INSERT INTO subject_statistics (department, academic_year, subject_id, pass_rate_percentage, average_grade, total_enrolled_students, year_of_study, semester, professor_id)
VALUES ('FTN', '2024/2025', 'NAIS', 95.5, 8.33, 120, 3, 5, 101);

INSERT INTO subject_statistics (department, academic_year, subject_id, pass_rate_percentage, average_grade, total_enrolled_students, year_of_study, semester, professor_id)
VALUES ('FTN', '2024/2025', 'OOP', 87.2, 7.85, 150, 2, 3, 102);

INSERT INTO subject_statistics (department, academic_year, subject_id, pass_rate_percentage, average_grade, total_enrolled_students, year_of_study, semester, professor_id)
VALUES ('FTN', '2024/2025', 'WEB', 78.5, 7.12, 80, 3, 6, 103);

-- Test performanse profesora
INSERT INTO professor_performance (professor_id, academic_year, total_students_taught, total_subjects_taught, total_students_passed, total_students_failed, overall_pass_rate, average_grade_given, total_exams_conducted, hardest_subject_id, hardest_subject_name, hardest_subject_pass_rate, easiest_subject_id, easiest_subject_name, easiest_subject_pass_rate, performance_consistency_score)
VALUES (101, '2024/2025', 120, 1, 115, 5, 95.8, 8.33, 125, 'NAIS', 'Napredni algoritmi i strukture podataka', 95.8, 'NAIS', 'Napredni algoritmi i strukture podataka', 95.8, 0.92);

INSERT INTO professor_performance (professor_id, academic_year, total_students_taught, total_subjects_taught, total_students_passed, total_students_failed, overall_pass_rate, average_grade_given, total_exams_conducted, hardest_subject_id, hardest_subject_name, hardest_subject_pass_rate, easiest_subject_id, easiest_subject_name, easiest_subject_pass_rate, performance_consistency_score)
VALUES (102, '2024/2025', 150, 1, 130, 20, 86.7, 7.85, 155, 'OOP', 'Objektno orijentisano programiranje', 86.7, 'OOP', 'Objektno orijentisano programiranje', 86.7, 0.85);

INSERT INTO professor_performance (professor_id, academic_year, total_students_taught, total_subjects_taught, total_students_passed, total_students_failed, overall_pass_rate, average_grade_given, total_exams_conducted, hardest_subject_id, hardest_subject_name, hardest_subject_pass_rate, easiest_subject_id, easiest_subject_name, easiest_subject_pass_rate, performance_consistency_score)
VALUES (103, '2024/2025', 80, 1, 65, 15, 81.3, 7.12, 85, 'WEB', 'Web programiranje', 81.3, 'WEB', 'Web programiranje', 81.3, 0.78);