package cli.tutoeasy.command;

import cli.tutoeasy.model.dto.LoginRequestDto;
import cli.tutoeasy.service.AuthService;
import picocli.CommandLine.*;

@Command(name = "login")
public class LoginCommand implements Runnable {

    @Option(names = "--email", required = true)
    private String email;

    @Option(names = "--password", required = true, interactive = true)
    private String password;

    private final AuthService authService;

    public LoginCommand(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public void run() {
        LoginRequestDto req = new LoginRequestDto(email, password);
        var res = authService.login(req);
        System.out.println(res.message());
    }
}
