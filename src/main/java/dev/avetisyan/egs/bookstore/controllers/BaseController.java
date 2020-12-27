package dev.avetisyan.egs.bookstore.controllers;

import dev.avetisyan.egs.bookstore.dtos.request.general.SortCriteria;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorCode;
import dev.avetisyan.egs.bookstore.dtos.response.general.ErrorDto;
import dev.avetisyan.egs.bookstore.dtos.response.general.ResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class BaseController {
    @ApiIgnore
    protected ResponseEntity<ResponseDto> generateResponse(ResponseDto response) {
        if (response.getErrors() == null || response.getErrors().isEmpty())
            return ResponseEntity.ok(response);

        return generateErrorResponse(response);
    }

    @ApiIgnore
    protected ResponseEntity<ResponseDto> generateCreatedResponse(ResponseDto response) {
        if (response.getErrors() == null || response.getErrors().isEmpty())
            return new ResponseEntity<>(response, HttpStatus.CREATED);

        return generateErrorResponse(response);
    }

    @ApiIgnore
    protected ResponseEntity<ResponseDto> generateErrorResponse(ResponseDto response) {
        if (response.getErrors().stream().anyMatch(e -> e.getErrorCode() == ErrorCode.ERR_NF))
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

        if (response.getErrors().stream().
                anyMatch(e -> e.getErrorCode() == ErrorCode.ERR_DUP || e.getErrorCode() == ErrorCode.ERR_NA
                        || e.getErrorCode() == ErrorCode.ERR_LE || e.getErrorCode() == ErrorCode.ERR_DM))
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

        if (response.getErrors().stream().anyMatch(e -> e.getErrorCode() == ErrorCode.ERR_AD))
            return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);

        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ApiIgnore
    protected ResponseEntity<ResponseDto> getBindingErrorsIfExist(
            BindingResult result, Map<String, ErrorCode> fieldErrorMapping) {

        Set<ErrorDto> errors = new HashSet<>();
        fieldErrorMapping.forEach((fieldName, errorCode) -> {
            FieldError error = result.getFieldError(fieldName);
            if (error != null) errors.add(new ErrorDto(errorCode, error.getDefaultMessage()));
        });

        return errors.isEmpty() ? null : ResponseEntity.badRequest().body(ResponseDto.error(errors));
    }

    @ApiIgnore
    protected ResponseEntity<ResponseDto> getSortingFieldErrorIfExists(@NotNull SortCriteria sortCriteria,
                                                                       @NotNull Set<String> allowedSortFields) {

        if (sortCriteria.getSortField() == null || allowedSortFields.contains(sortCriteria.getSortField()))
            return null;

        return ResponseEntity.badRequest().body(ResponseDto.error(new ErrorDto(ErrorCode.ERR_NA,
                "Allowed sorting fields: " + String.join(",", allowedSortFields))));
    }

}
