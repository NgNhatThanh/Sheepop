package com.app.bdc_backend.model.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
@Setter
public class PageResponse<T> {

    private int pageNumber;

    private int pageSize;

    private int totalElements;

    private int totalPages;

    private int numberOfElements;

    private List<T> content;

    public PageResponse(Page<T> page) {
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.totalElements = page.getNumberOfElements();
        this.totalPages = page.getTotalPages();
        this.numberOfElements = page.getNumberOfElements();
        this.content = page.getContent();
    }

}
