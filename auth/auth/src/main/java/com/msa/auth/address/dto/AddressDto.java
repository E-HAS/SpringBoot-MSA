package com.msa.auth.address.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {
    private Integer seq;
    private String addressName;
    
    private Integer sidoCode;
    private Integer gugunCode;
    private Integer dongCode;
    private Integer riCode;
    
}
