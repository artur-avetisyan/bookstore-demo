package dev.avetisyan.egs.bookstore.dtos.request;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@ApiModel(description = "Used as a request body model for users")
public class UserRequestDto implements Serializable {

    @Serial
    public static final long serialVersionUID = 1L;

    @Size(max = 30, message = "Username can have max length of {max}.")
    private String username;
    @Size(max = 320, message = "Email can have max length of {max}.")
    private String email;
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
