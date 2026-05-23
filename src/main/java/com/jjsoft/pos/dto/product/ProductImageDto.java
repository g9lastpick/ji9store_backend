package com.jjsoft.pos.dto.product;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProductImageDto {
    private Long imageId;
    private String imageUrl;
}