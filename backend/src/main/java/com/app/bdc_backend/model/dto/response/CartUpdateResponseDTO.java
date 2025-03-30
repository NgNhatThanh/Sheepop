package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
@Setter
public class CartUpdateResponseDTO {

    private CartDTO cart;

    private String warnMsg;

}
