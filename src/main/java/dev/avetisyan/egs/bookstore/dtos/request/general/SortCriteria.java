package dev.avetisyan.egs.bookstore.dtos.request.general;

import io.swagger.annotations.ApiModel;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(description = "Sort criteria for get requests")
public class SortCriteria implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Sort.Direction sortDir = Sort.Direction.ASC;
    private String sortField;

    public Sort.Direction getSortDir() {
        return sortDir;
    }

    public void setSortDir(Sort.Direction sortDir) {
        this.sortDir = sortDir;
    }

    public String getSortField() {
        return sortField;
    }

    public void setSortField(String sortField) {
        this.sortField = sortField;
    }
}
