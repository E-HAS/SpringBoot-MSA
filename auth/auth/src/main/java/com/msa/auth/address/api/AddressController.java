package com.msa.auth.address.api;

import com.msa.auth.address.dto.AddressDto;
import com.msa.auth.address.entity.AddressEntity;
import com.msa.auth.address.service.AddressServiceImpt;
import com.msa.auth.common.dto.ResponseDto;
import com.msa.auth.common.exception.code.BusinessExceptionErrorCode;
import com.msa.auth.common.exception.error.BusinessException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/address")
@RequiredArgsConstructor
public class AddressController {
	private final AddressServiceImpt addressServiceImpt;
	@GetMapping
	public ResponseEntity<ResponseDto> getAddressAll(@PageableDefault(size = 15) Pageable pageable){
		Page<AddressDto> lists = addressServiceImpt.findAll(AddressDto.builder().build(), pageable);

		if(lists.isEmpty()){
		 	throw new BusinessException(BusinessExceptionErrorCode.NOT_FOUND,"Address Not Found");
		}

		return ResponseEntity.status(HttpStatus.OK)
					.body(ResponseDto.builder()
					.status(HttpStatus.OK.value())
					.message(HttpStatus.OK.getReasonPhrase())
					.data(Map.of("address", lists))
					.build());

	}
	
	@GetMapping(path="/{addressSeq}")
	public ResponseEntity<ResponseDto> getAddress(@PathVariable ("addressSeq") Integer addressSeq){

		AddressEntity findEntity = addressServiceImpt.findBySeq(AddressDto.builder().seq(addressSeq).build());
		if(findEntity ==null){
			throw new BusinessException(BusinessExceptionErrorCode.NOT_FOUND,"Address Not Found");
		}

		return ResponseEntity.status(HttpStatus.OK)
								.body(ResponseDto.builder()
								.status(HttpStatus.OK.value())
								.message(HttpStatus.OK.getReasonPhrase())
								.data(Map.of("address", findEntity))
								.build());
		
	}
}
