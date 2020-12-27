package dev.avetisyan.egs.bookstore.dtos.request;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;

@ApiModel(description = "Used as a request body for comments")
public class CommentRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 300, message = "Comment can have max length of {max}.")
    private String text;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
