package dev.avetisyan.egs.bookstore.dtos.request;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ApiModel(description = "Used as a request body in the author controller")
public class AuthorRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 120, message = "FullName can have max length of {max}.")
    private String fullName;
    private Date birthDate;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

}
