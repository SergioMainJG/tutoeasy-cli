package cli.tutoeasy.service;

import cli.tutoeasy.config.Argon2Util;
import cli.tutoeasy.model.entities.User;
import cli.tutoeasy.model.dto.*;
import cli.tutoeasy.repository.UserRepository;

public class AuthService {

    private final UserRepository userRepo;

    public AuthService(UserRepository repo) {
        this.userRepo = repo;
    }

    public LoginResponseDto login(LoginRequestDto req) {

        User user = userRepo.findByEmail(req.email());
        if (user == null)
            return new LoginResponseDto(0, "", "", "Invalid credentials");

        if (!Argon2Util.verifyPassword(req.password(), user.getPasswordHash()))
            return new LoginResponseDto(0, "", "", "Invalid credentials");

        return new LoginResponseDto(
                user.getId(),
                user.getUsername(),
                user.getRol().name(),
                "Login successful"
        );
    }
}
