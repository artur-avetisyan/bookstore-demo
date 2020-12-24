package dev.avetisyan.egs.bookstore.dtos.response.general;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(description = "Error codes and human readable descriptive messages")
public class ErrorDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private ErrorCode errorCode;
    private String errorMessage;

    public static ErrorDto internalError() {
        return new ErrorDto(ErrorCode.ERR_GEN, "Internal error occurred");
    }

    public ErrorDto() {
    }

    public ErrorDto(String errorMessage) {
        this(ErrorCode.ERR_GEN, errorMessage);
    }

    public ErrorDto(ErrorCode errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
