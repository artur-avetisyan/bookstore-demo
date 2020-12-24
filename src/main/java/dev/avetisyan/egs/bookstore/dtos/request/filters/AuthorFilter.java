package dev.avetisyan.egs.bookstore.dtos.request.filters;

import java.io.Serial;

public class AuthorFilter extends ApprovedFilter {

    @Serial
    private final static long serialVersionUID = 1L;

    private String fullName;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
