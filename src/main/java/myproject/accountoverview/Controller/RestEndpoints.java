package myproject.accountoverview.Controller;

import lombok.RequiredArgsConstructor;
import myproject.accountoverview.application.LoginService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This class holds the Rest endpoints and uses the LoginService class
 * to handle the request and serve data.
 */

@RestController
@RequiredArgsConstructor
public class RestEndpoints {

    private final LoginService loginService;

    /**
     * Endpoint to check if server is running.
     * @return Message from the server.
     */
    @GetMapping("/")
    public String init(){
        return "Server is running!";
    }

    /**
     * Starts the connection to nordnet service.
     * @return Returns a link to bankid authorization or error message.
     */
    @GetMapping("/login/bankid")
    public String login(){
        return loginService.login();
    }

    /**
     * Checks status of bankid authorization.
     * @return Message from Nordnet API or error message.
     */
    @GetMapping("/login/bankid/status")
    public String loginStatus(){
        return loginService.status();
    }

    /**
     * Shows all account details fetched from nordnet.
     * @return Account overview or error message.
     */
    @GetMapping("/account/overview")
    public String accountOverview(){
        return loginService.accountOverview();
    }
}
