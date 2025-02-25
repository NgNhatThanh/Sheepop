package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MyPageImpl<T> {

    private int totalElements;

    private List<T> content;

}
