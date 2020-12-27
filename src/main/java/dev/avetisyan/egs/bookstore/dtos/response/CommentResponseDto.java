package dev.avetisyan.egs.bookstore.dtos.response;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;
import java.sql.Timestamp;

@ApiModel(description = "Used as a response model for comments")
public class CommentResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private long commenterId;
    private String text;
    private Timestamp commentedAt;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCommenterId() {
        return commenterId;
    }

    public void setCommenterId(long commenterId) {
        this.commenterId = commenterId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getCommentedAt() {
        return commentedAt;
    }

    public void setCommentedAt(Timestamp commentedAt) {
        this.commentedAt = commentedAt;
    }
}
