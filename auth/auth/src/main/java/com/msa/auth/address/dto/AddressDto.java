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
    private long addressCode;
    private String addressName;
    
    private Integer sidoCode;
    private String sidoNm;
    
    private Integer sigugunCode;
    private String sigugunNm;

    private Integer eupmyeondongCode;
    private String eupmyeondongNm;

    private Integer status;
    
}
