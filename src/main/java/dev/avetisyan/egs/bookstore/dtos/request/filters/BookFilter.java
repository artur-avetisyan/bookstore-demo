package dev.avetisyan.egs.bookstore.dtos.request.filters;

import java.io.Serial;

public class BookFilter extends ApprovedFilter {

    @Serial
    private final static long serialVersionUID = 1L;

    private String name;
    private String description;
    private Long creatorId;
    private Integer authorId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }
}
