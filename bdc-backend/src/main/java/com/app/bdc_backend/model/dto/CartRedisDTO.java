package com.app.bdc_backend.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class CartRedisDTO {

    private String id;

    private String username;

    private Date updatedAt;

}
