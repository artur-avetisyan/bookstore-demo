package dev.avetisyan.egs.bookstore.dtos.request;

import io.swagger.annotations.ApiModel;

import javax.validation.constraints.Size;
import java.io.Serial;
import java.io.Serializable;
import java.sql.Date;

@ApiModel(description = "Used as a request body in the book controller")
public class BookRequestDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Size(max = 100, message = "Book name '${validatedValue}' can have max length of {max}.")
    private String name;
    private Date publishDate;
    private Integer authorId;
    private double price;
    @Size(max = 300, message = "Book description '${validatedValue}' can have max length of {max}.")
    private String description;

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

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
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
}
