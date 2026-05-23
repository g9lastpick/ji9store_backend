package com.jjsoft.pos.dto.draw;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 드로우 조회 조건 DTO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DrawSearchRequestDto {

	@NotNull(message = "storeId는 필수입니다.")
    private Long storeId;
	@NotNull(message = "locationId는 필수입니다.")
    private Long locationId;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;
    
    private String status;
}