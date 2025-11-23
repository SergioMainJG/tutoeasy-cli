package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.dto.CreateAdministradorDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

public class AdministradorService {
    private final UserRepository repo;

    public AdministradorService(UserRepository repo) {
        this.repo = repo;
    }

    public void createAdministrador(CreateAdministradorDto dto) {

        User existing = repo.findByEmail(dto.email());
        if (existing != null) {
            System.out.println("Ya existe un administrador registrado con ese email.");
            return;
        }

        User admin = new User();
        admin.setUsername(dto.name() + " " + dto.lastName());
        admin.setEmail(dto.email());
        admin.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        admin.setRol(UserRole.admin);

        repo.save(admin);

        System.out.println(" Administrador registrado exitosamente.");
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