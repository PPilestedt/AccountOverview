package myproject.accountoverview.application;


import myproject.accountoverview.domain.ResponseTokenDto;
import org.springframework.stereotype.Service;

/**
 * LoginService uses NordnetHttpHandler to orchestrate the login, retrieval
 * of account information and returns response strings and error codes.
 */

@Service
public class LoginService {

    private final NordnetHttpHandler okhttp;
    private final String autostartURL = "bankid:///?autostarttoken=";
    private ResponseTokenDto responseDto;


    public LoginService() {
        okhttp = new NordnetHttpHandler();
    }

    /**
     * Handles the initial http request and the creation of the bankid qr-code string.
     * @return QR-code autostart URL
     */
    public String login() {
        if(!okhttp.loginAnonymous() || !okhttp.authenticationStart()){
            return okhttp.getErrorMsg();
        }

        responseDto = okhttp.getResponseTokenDto();

        if(responseDto == null || responseDto.getAuto_start_token().length() == 0) {
            return "Error: auto_start_token missing";
        }
        return autostartURL + this.responseDto.getAuto_start_token();
    }

    /**
     * Checks if the login is completed.
     * @return the message sent from nordnet or an error message.
     */
    public String status() {
        if(responseDto == null || responseDto.getOrder_ref().length() == 0){
            return "Order-ref is missing, unable to process request. Try ../login/bankid again";
        }
        if(!okhttp.poll(responseDto.getOrder_ref())){
            return okhttp.getErrorMsg();
        }
        return okhttp.getResponseBody();
    }

    /**
     * Retrieves the account overview.
     * @return account overview or error message.
     */
    public String accountOverview() {

        if(!okhttp.retrieveBatch()){
            return okhttp.getErrorMsg();
        }
        return okhttp.getResponseBody();
    }
}
