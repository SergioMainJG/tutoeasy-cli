drop schema if exists `tutoeasy-capstone-mysql`;
create schema if not exists `tutoeasy-capstone-mysql`;
use  `tutoeasy-capstone-mysql`;
CREATE TABLE Careers (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE Subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE Topics (
    id INT AUTO_INCREMENT PRIMARY KEY,
    subjectId INT NOT NULL,
    name VARCHAR(50) NOT NULL,
    FOREIGN KEY (subjectId) REFERENCES Subjects(id)
);

CREATE TABLE Users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    passwordHash VARCHAR(255) NOT NULL,
    rol ENUM('admin', 'tutor', 'student') NOT NULL, 
    careerId INT,
    description TEXT,
    FOREIGN KEY (careerId) REFERENCES Careers(id)
);
CREATE TABLE TutorExpertise (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tutorId INT NOT NULL,
    subjectId INT NOT NULL,
    FOREIGN KEY (tutorId) REFERENCES Users(id),
    FOREIGN KEY (subjectId) REFERENCES Subjects(id),
    UNIQUE KEY unique_expertise (tutorId, subjectId)
);

CREATE TABLE TutorSchedule (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tutorId INT NOT NULL,
    dayOfWeek INT NOT NULL, -- 1=Monday, 7=Sunday
    startTime TIME NOT NULL,
    endTime TIME NOT NULL,
    FOREIGN KEY (tutorId) REFERENCES Users(id)
);

CREATE TABLE Tutorings (
    id INT AUTO_INCREMENT PRIMARY KEY,
    studentId INT NOT NULL,
    tutorId INT NOT NULL,
    subjectId INT NOT NULL,
    topicId INT,
    status ENUM('confirmed', 'unconfirmed', 'canceled', 'completed') NOT NULL, 
    meetingDate DATE NOT NULL,
    meetingTime TIME NOT NULL,
    FOREIGN KEY (studentId) REFERENCES Users(id),
    FOREIGN KEY (tutorId) REFERENCES Users(id),
    FOREIGN KEY (subjectId) REFERENCES Subjects(id),
    FOREIGN KEY (topicId) REFERENCES Topics(id)
);

CREATE TABLE SessionFeedback (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tutoringId INT UNIQUE NOT NULL,
    studentId INT NOT NULL,
    tutorId INT NOT NULL,
    rating INT,
    isTutorObservation BOOLEAN DEFAULT FALSE,
    comment TEXT NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (tutoringId) REFERENCES Tutorings(id),
    FOREIGN KEY (studentId) REFERENCES Users(id),
    FOREIGN KEY (tutorId) REFERENCES Users(id)
);


CREATE TABLE Messages (
    id INT AUTO_INCREMENT PRIMARY KEY,
    senderId INT NOT NULL,
    receiverId INT NOT NULL,
    content TEXT NOT NULL,
    createdAt DATETIME NOT NULL,
    wasRead BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (senderId) REFERENCES Users(id),
    FOREIGN KEY (receiverId) REFERENCES Users(id)
);

CREATE TABLE Notifications (
    id INT AUTO_INCREMENT PRIMARY KEY,
    userId INT NOT NULL,
    message TEXT NOT NULL,
    createdAt DATETIME NOT NULL,
    wasReaded BOOLEAN DEFAULT FALSE,
    type VARCHAR(20),
    FOREIGN KEY (userId) REFERENCES Users(id)
);

CREATE TABLE Reports (
    id INT AUTO_INCREMENT PRIMARY KEY,
    createdByAdmin INT NOT NULL,
    reportType VARCHAR(50) NOT NULL,
    content TEXT NOT NULL,
    createdAt DATETIME NOT NULL,
    FOREIGN KEY (createdByAdmin) REFERENCES Users(id)
);

CREATE INDEX idx_tutoring_tutor_status ON Tutorings(tutorId, status);
CREATE INDEX idx_tutoring_schedule ON Tutorings(tutorId, meetingDate, meetingTime, status);

CREATE INDEX idx_user_email ON Users(email);
CREATE INDEX idx_user_username ON Users(username);
CREATE INDEX idx_user_role ON Users(rol);

CREATE INDEX idx_topic_subject ON Topics(subjectId);

CREATE INDEX idx_expertise_tutor ON TutorExpertise(tutorId);
CREATE INDEX idx_expertise_subject ON TutorExpertise(subjectId);

CREATE INDEX idx_schedule_tutor ON TutorSchedule(tutorId);
CREATE INDEX idx_schedule_tutor_day ON TutorSchedule(tutorId, dayOfWeek);

CREATE INDEX idx_tutoring_student ON Tutorings(studentId);
CREATE INDEX idx_tutoring_subject ON Tutorings(subjectId);
CREATE INDEX idx_tutoring_date ON Tutorings(meetingDate);
CREATE INDEX idx_tutoring_student_status ON Tutorings(studentId, status);

CREATE INDEX idx_feedback_tutoring ON SessionFeedback(tutoringId);
CREATE INDEX idx_feedback_student ON SessionFeedback(studentId);
CREATE INDEX idx_feedback_tutor ON SessionFeedback(tutorId);
CREATE INDEX idx_feedback_created ON SessionFeedback(createdAt);
CREATE INDEX idx_feedback_tutor_rating ON SessionFeedback(tutorId, rating);

CREATE INDEX idx_message_sender ON Messages(senderId);
CREATE INDEX idx_message_receiver ON Messages(receiverId);
CREATE INDEX idx_message_receiver_read ON Messages(receiverId, wasRead);
CREATE INDEX idx_message_created ON Messages(createdAt);
CREATE INDEX idx_message_conversation ON Messages(senderId, receiverId, createdAt);

CREATE INDEX idx_notification_user ON Notifications(userId);
CREATE INDEX idx_notification_user_read ON Notifications(userId, wasReaded);
CREATE INDEX idx_notification_created ON Notifications(createdAt);
CREATE INDEX idx_notification_type ON Notifications(type);

CREATE INDEX idx_report_admin ON Reports(createdByAdmin);
CREATE INDEX idx_report_type ON Reports(reportType);
CREATE INDEX idx_report_created ON Reports(createdAt);

ANALYZE TABLE Users;
ANALYZE TABLE Careers;
ANALYZE TABLE Subjects;
ANALYZE TABLE Topics;
ANALYZE TABLE TutorExpertise;
ANALYZE TABLE TutorSchedule;
ANALYZE TABLE Tutorings;
ANALYZE TABLE SessionFeedback;
ANALYZE TABLE Messages;
ANALYZE TABLE Notifications;
ANALYZE TABLE Reports;

ALTER TABLE Users ADD INDEX idx_users_email (email);
ALTER TABLE Users ADD INDEX idx_users_username (username);
ALTER TABLE Users ADD INDEX idx_users_email_rol (email, rol);
SHOW INDEX FROM Users;
ANALYZE TABLE Users;

SHOW VARIABLES LIKE 'table_open_cache';
SHOW VARIABLES LIKE 'skip_name_resolve';
SHOW VARIABLES LIKE 'performance_schema';
SHOW VARIABLES LIKE 'innodb_file_per_table';
