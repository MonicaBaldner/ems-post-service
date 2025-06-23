package com.algaworks.algaposts.ems_post_service.api.model;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Builder
@Data
public class PostResult {
    private String postId;
    private Integer wordCount;
    private BigDecimal calculatedValue;
}
