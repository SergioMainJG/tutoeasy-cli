package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.dto.CreateStudentDto;
import cli.tutoeasy.model.dto.CreateUserDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

public class StudentService {

    private final UserRepository repo;

    public StudentService(UserRepository repo) {
        this.repo = repo;
    }
    public void createStudent(CreateStudentDto dto) {

        User existing = repo.findByEmail(dto.email());
        if (existing != null) {
            System.out.println("There is already a registered student with this email.");
            return;
        }

        User student = new User();
        student.setUsername(dto.name());
        student.setEmail(dto.email());
        student.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        student.setRol(UserRole.student);

        repo.save(student);

        System.out.println("student successfully registered.");
    }

    public User getStudentById(int id) {
        return repo.findById(id);
    }

    public void updateStudent(User student) {
        repo.update(student);
    }

    public void deleteStudent(int id) {
        repo.delete(id);
    }
}