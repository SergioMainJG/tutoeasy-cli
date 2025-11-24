package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.dto.CreateAdministratorDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

public class AdministratorService {
    private final UserRepository repo;

    public AdministratorService(UserRepository repo) {
        this.repo = repo;
    }

    public void createAdministrator(CreateAdministratorDto dto) {

        User existing = repo.findByEmail(dto.email());
        if (existing != null) {
            System.out.println("There is already a registered administrator with that email address.");
            return;
        }

        User admin = new User();
        admin.setUsername(dto.name() + " " + dto.lastName());
        admin.setEmail(dto.email());
        admin.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        admin.setRol(UserRole.admin);

        repo.save(admin);

        System.out.println("Administrator successfully registered.");
    }

    public User getAdminById(int id) {
        return repo.findById(id);
    }

    public void updateAdmin(User admin) {
        repo.update(admin);
    }

    public void deleteAdmin(int id) {
        repo.delete(id);
    }
}