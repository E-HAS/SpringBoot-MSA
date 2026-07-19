package com.msa.gateway.jwt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class JwtToken {
	private String prefix;
	private String accessTokenId;
	private String refreshTokenId;
	private String accessToken;
	private String refreshToken;
}
