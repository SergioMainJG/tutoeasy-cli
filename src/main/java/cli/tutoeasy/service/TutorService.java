package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.dto.CreateTutorDto;
import cli.tutoeasy.model.dto.CreateUserDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

public class TutorService {

    private final UserRepository repo;

    public TutorService(UserRepository repo) {
        this.repo = repo;
    }
    public void createTutor(CreateTutorDto dto) {

        User existing = repo.findByEmail(dto.email());
        if (existing != null) {
            System.out.println("The tutor is already registered with that email.");
            return;
        }

        User tutor = new User();
        tutor.setUsername(dto.name());
        tutor.setEmail(dto.email());
        tutor.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        tutor.setRol(UserRole.tutor);

        repo.save(tutor);

        System.out.println("Tutor successfully registered.");
    }

    public User getTutorById(int id) {
        return repo.findById(id);
    }

    public void updateTutor(User tutor) {
        repo.update(tutor);
    }

    public void deleteTutor(int id) {
        repo.delete(id);
    }



}