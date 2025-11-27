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
        if(res.userId() == -1){
            String msg = Help.Ansi.AUTO.string("@|red Invalid username or password|@");
            System.out.println(msg);
            System.exit(0);
        }
        String msg = Help.Ansi.AUTO.string("@|green Welcome back,|@ @|blue "+  res.username() + "|@");
        System.out.println(msg);
    }
}
