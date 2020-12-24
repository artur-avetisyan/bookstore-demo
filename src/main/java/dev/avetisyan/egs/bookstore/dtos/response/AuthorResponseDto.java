package dev.avetisyan.egs.bookstore.dtos.response;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ApiModel(description = "Used as a response data in the author controller")
public class AuthorResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int id;
    private String fullName;
    private Date birthDate;
    private boolean isApproved;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }
}
