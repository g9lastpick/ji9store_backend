package com.jjsoft.pos.dto.common;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

//예시 DTO
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CodeValueDto {
 private String label; // ex) storeId
 private String value; // ex) storeName
}
