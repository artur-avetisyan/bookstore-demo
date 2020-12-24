package dev.avetisyan.egs.bookstore.dtos.response.general;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiModel(description = "General response structure")
public class ResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Serializable data;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Long total;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Set<ErrorDto> errors;

    public ResponseDto() {
    }

    private ResponseDto(Serializable data, Long total, Set<ErrorDto> errors) {
        this.data = data;
        this.total = total;
        this.errors = errors;
    }

    public static ResponseDto success(Serializable data) {
        return new ResponseDto(data, null, null);
    }

    public static ResponseDto success(Serializable data, long total) {
        return new ResponseDto(data, total, null);
    }

    public static ResponseDto error(ErrorDto error) {
        return ResponseDto.error(Stream.of(error).collect(Collectors.toUnmodifiableSet()));
    }

    public static ResponseDto error(Set<ErrorDto> errors) {
        return new ResponseDto(null, null, errors);
    }

    public Serializable getData() {
        return data;
    }

    public void setData(Serializable data) {
        this.data = data;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Set<ErrorDto> getErrors() {
        return errors;
    }

    public void setErrors(Set<ErrorDto> errors) {
        this.errors = errors;
    }

}
