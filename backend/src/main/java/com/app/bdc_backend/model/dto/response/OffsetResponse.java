package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class OffsetResponse<T> {

    private List<T> content;

    private int nextOffset;

    public OffsetResponse(List<T> content, int nextOffset) {
        this.content = content;
        this.nextOffset = nextOffset;
    }

}
