package myproject.accountoverview.application;

import com.fasterxml.jackson.databind.ObjectMapper;
import myproject.accountoverview.domain.ResponseTokenDto;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * NordnetHttpHandler uses okhttp3 to perform requests to the Nordnet API.
 */

public class NordnetHttpHandler {

    private String ntag = "";
    private String responseBody;
    private String errorMessage;
    private ResponseTokenDto responseTokenDto;

    private final Headers generalHeaders;
    private final OkHttpClient httpClient;

    private final String loginAnonymousURL = "https://www.nordnet.se/api/2/login/anonymous";
    private final String authenticationURL = "https://www.nordnet.se/api/2/authentication/eid/se/bankid/start";
    private final String pollURL = "https://www.nordnet.se/api/2/authentication/eid/se/bankid/poll";
    private final String batchURL = "https://www.nordnet.se/api/2/batch";

    private final String batchBodyJson =    """
                                            {"batch":"[{\\"relative_url\\":\\"accounts\\",\\"method\\":\\"GET\\"},{\\"relative_url\\":\\"customers/contact_info\\",\\"method\\":\\"GET\\"}]"}
                                            """;

    public NordnetHttpHandler() {

        MyCookieJar cookieJar = new MyCookieJar();
        httpClient = new OkHttpClient().newBuilder().cookieJar(cookieJar).build();

        generalHeaders = new Headers.Builder()
                .add("accept", "application/json")
                .add("client-id", "NEXT")
                .add("Sec-Fetch-Site", "same-origin")
                .add("Sec-Fetch-Mode", "cors")
                .add("Sec-Fetch-Dest", "empty")
                .add("Referer", "https://www.nordnet.se/")
                .add("Accept-Language", "sv-SE,sv;q=0.9,en-US;q=0.8,en;q=0.7")
                .build();
    }

    /**
     * Starts the login procedure by initiating contact with nordnet.
     * @return true if the connection is successful or false if not.
     */
    public boolean loginAnonymous() {

        Request request = new Request.Builder()
                .url(loginAnonymousURL)
                .headers(generalHeaders)
                .addHeader("ntag",ntag)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }
            ntag = response.header("ntag");

        } catch (IOException e) {
            errorMessage = e.getMessage();
            return false;
        }
        return true;
    }

    /**
     * Requests the autostart token from nordnet.
     * @return true if successful and false if unsuccessful.
     */
    public boolean authenticationStart(){

        Request request = new Request.Builder()
                .url(authenticationURL)
                .headers(generalHeaders)
                .addHeader("ntag", ntag)
                .post(RequestBody.create("", null))
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected code " + response);
            }

            String body = Objects.requireNonNull(response.body()).string();
            responseTokenDto = new ObjectMapper().readValue(body, ResponseTokenDto.class);

        }catch (IOException e) {
            errorMessage = e.getMessage();
            return false;
        }
        return true;
    }

    /**
     * Checks if the login is successful and stores the response body.
     * @param order_ref Reference number of the login
     * @return true if successful and false if unsuccessful.
     */
    public boolean poll(String order_ref) {

        RequestBody formBody = new FormBody.Builder().add("order_ref",order_ref).build();
        Request request = new Request.Builder()
                .url(pollURL)
                .headers(generalHeaders)
                .addHeader("ntag",ntag)
                .addHeader("content-type", "application/x-www-form-urlencoded")
                .addHeader("sub-client-id", "NEXT")
                .addHeader("Origin", "https://www.nordnet.se")
                .post(formBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()){
                throw new IOException("Unexpected code " + response);
            }

            if(response.header("ntag") != null){
                ntag = response.header("ntag");
            }

            responseBody = Objects.requireNonNull(response.body()).string();
            return true;

        }catch (IOException e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    /**
     * Retrieves the account information from nordnet and stores it in response body.
     * @return true if succesful or false if unsuccessful.
     */
    public boolean retrieveBatch() {

        RequestBody jsonBody = RequestBody.create(batchBodyJson,MediaType.parse("application/json"));
        Request request = new Request.Builder()
                .url(batchURL)
                .headers(generalHeaders)
                .addHeader("x-nn-href", "https://www.nordnet.se/oversikt")
                .addHeader("ntag",ntag)
                .addHeader("content-type", "application/json")
                .post(jsonBody)
                .build();

        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            responseBody = Objects.requireNonNull(response.body()).string();
            return true;

        }catch (IOException e) {
            errorMessage = e.getMessage();
            return false;
        }
    }

    public ResponseTokenDto getResponseTokenDto() {
        return responseTokenDto;
    }

    /**
     * Returns the latest error message
     * @return Latest error message
     */
    public String getErrorMsg() {
        return errorMessage;
    }

    /**
     * Retrieves the latest response body
     * @return string with the latest response
     */
    public String getResponseBody() {
        return responseBody;
    }
}
