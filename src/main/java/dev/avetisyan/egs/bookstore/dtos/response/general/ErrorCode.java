package dev.avetisyan.egs.bookstore.dtos.response.general;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(description = "Error codes for specific error cases")
public enum ErrorCode implements Serializable {
    ERR_GEN("Generic error"),
    ERR_DUP("Duplication error"),
    ERR_NF("Not found"),
    ERR_AD("Access denied"),
    ERR_LE("Length exceeded"),
    ERR_NA("Not allowed");

    @Serial
    private static final long serialVersionUID = 1L;

    private String description;

    ErrorCode(String description) {
        this.description = description;
    }
}
