package myproject.accountoverview.domain;

import com.fasterxml.jackson.annotation.JsonCreator;

public class ResponseTokenDto {

    private String auto_start_token;
    private String order_ref;

    @JsonCreator
    public ResponseTokenDto() {
    }

    public String getAuto_start_token() {
        return auto_start_token;
    }

    public void setAuto_start_token(String auto_start_token) {
        this.auto_start_token = auto_start_token;
    }

    public String getOrder_ref() {
        return order_ref;
    }

    public void setOrder_ref(String order_ref) {
        this.order_ref = order_ref;
    }
}
