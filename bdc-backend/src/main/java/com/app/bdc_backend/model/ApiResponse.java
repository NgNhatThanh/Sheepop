package com.app.bdc_backend.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {

    private int code;

    private boolean error;

    private String errorMsg;

    private T data;

}
