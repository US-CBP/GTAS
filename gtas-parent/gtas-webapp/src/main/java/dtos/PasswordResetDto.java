package dtos;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class PasswordResetDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final String EMPTY_FIELD_ERROR = "The request has missing information, please try again.";
    private static final String INVALID_CREDENTIALS_REQUEST_ERROR = "Invalid request. Please try again.";

    @NotEmpty(message = EMPTY_FIELD_ERROR)
    private String username;

    @NotEmpty(message = EMPTY_FIELD_ERROR)
    private String password;

    @NotEmpty(message = EMPTY_FIELD_ERROR)
    private String passwordConfirm;

    @NotEmpty(message = INVALID_CREDENTIALS_REQUEST_ERROR)
    private String resetToken;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }


}
