package dev.avetisyan.egs.bookstore.dtos.response;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ApiModel(description = "Used as a response model for users")
public class UserResponseDto implements Serializable {

    @Serial
    public static final long serialVersionUID = 1L;

    private long id;
    private String username;
    private String email;
    private Date createdDate;
    private short roleId;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

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

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public short getRoleId() {
        return roleId;
    }

    public void setRoleId(short roleId) {
        this.roleId = roleId;
    }
}
