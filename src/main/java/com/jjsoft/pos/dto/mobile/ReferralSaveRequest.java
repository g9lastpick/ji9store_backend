package com.jjsoft.pos.dto.mobile;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ReferralSaveRequest {

	private String userId;
    private String referrerPhone;
    private String noneYn;
}
