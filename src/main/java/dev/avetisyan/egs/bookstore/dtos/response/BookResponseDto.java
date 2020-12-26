package dev.avetisyan.egs.bookstore.dtos.response;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ApiModel(description = "Used as a response data in the book controller")
public class BookResponseDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private long id;
    private String name;
    private Date publishDate;
    private double price;
    private String description;
    private boolean isApproved;
    private long creatorId;

    private AuthorResponseDto author;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(Date publishDate) {
        this.publishDate = publishDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isApproved() {
        return isApproved;
    }

    public void setApproved(boolean approved) {
        isApproved = approved;
    }

    public long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(long creatorId) {
        this.creatorId = creatorId;
    }

    public AuthorResponseDto getAuthor() {
        return author;
    }

    public void setAuthor(AuthorResponseDto author) {
        this.author = author;
    }
}
