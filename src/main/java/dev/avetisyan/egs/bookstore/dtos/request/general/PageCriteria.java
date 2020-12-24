package dev.avetisyan.egs.bookstore.dtos.request.general;

import io.swagger.annotations.ApiModel;

import java.io.Serial;
import java.io.Serializable;

@ApiModel(description = "Pagination criteria for get requests")
public class PageCriteria implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private int pageIndex = 0;
    private int pageSize = 20;

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
