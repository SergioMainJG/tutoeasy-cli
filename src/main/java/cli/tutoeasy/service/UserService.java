package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.dto.CreateUserDto;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.entities.UserRole;
import cli.tutoeasy.repository.UserRepository;

public class UserService {

    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public void createUser(CreateUserDto dto) {

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPasswordHash(Argon2Util.hashingPassword(dto.password()));
        user.setRol(UserRole.valueOf(dto.role().toUpperCase()));

        repo.save(user);
    }
}
