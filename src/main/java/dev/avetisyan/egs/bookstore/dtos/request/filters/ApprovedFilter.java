package dev.avetisyan.egs.bookstore.dtos.request.filters;

import java.io.Serial;
import java.io.Serializable;

public class ApprovedFilter implements Serializable {

    @Serial
    private final static long serialVersionUID = 1L;

    private Boolean isApproved;

    public Boolean getApproved() {
        return isApproved;
    }

    public void setApproved(Boolean approved) {
        isApproved = approved;
    }
}
