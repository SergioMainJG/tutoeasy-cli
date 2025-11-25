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