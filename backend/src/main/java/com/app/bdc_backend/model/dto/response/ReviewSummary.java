package com.app.bdc_backend.model.dto.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewSummary {

    private int[] countRatings;

    private int countWithContent;

    private int countWithMedia;

}
