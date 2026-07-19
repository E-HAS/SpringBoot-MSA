package com.msa.auth.address.service;

import com.msa.auth.address.dto.AddressDto;
import com.msa.auth.address.entity.AddressEntity;
import com.msa.auth.address.entity.QAddressEntity;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AddressServiceImpt {

	private final JPAQueryFactory queryFactory;

	@Transactional
	public Page<AddressDto> findAll(AddressDto addressDto, Pageable pageable){
		List<AddressDto> Addresses = queryFactory
								            .select(Projections.fields(AddressDto.class
								            		,QAddressEntity.addressEntity.seq
								            		,QAddressEntity.addressEntity.addressName
								            		,QAddressEntity.addressEntity.sidoCode
								            		,QAddressEntity.addressEntity.gugunCode
								            		,QAddressEntity.addressEntity.dongCode
								            		,QAddressEntity.addressEntity.riCode
								                ))
								             .from(QAddressEntity.addressEntity)
								             .where(getDefaultWheres(addressDto))
								             .offset(pageable.getOffset())
								             .limit(pageable.getPageSize())
								             .orderBy(QAddressEntity.addressEntity.seq.asc())
								             .fetch();
		
		long total = queryFactory.select(QAddressEntity.addressEntity.count())
								 .from(QAddressEntity.addressEntity)
								 .fetchOne();
		
		return new PageImpl<>(Addresses, pageable, total);
	}
	
	@Transactional
	public AddressEntity findBySeq(AddressDto addressDto){
		return queryFactory
	            .select(Projections.fields(AddressEntity.class
	            		,QAddressEntity.addressEntity.seq
	            		,QAddressEntity.addressEntity.addressName
	            		,QAddressEntity.addressEntity.sidoCode
	            		,QAddressEntity.addressEntity.gugunCode
	            		,QAddressEntity.addressEntity.dongCode
	            		,QAddressEntity.addressEntity.riCode
	                ))
	             .from(QAddressEntity.addressEntity)
	             .where(getDefaultWheres(addressDto))
	             .fetchOne();
	}
	
	private BooleanBuilder getDefaultWheres(AddressDto addressDto) {
		BooleanBuilder wheres = new BooleanBuilder();
		if (addressDto.getSeq() != null) {
	    	wheres.and(QAddressEntity.addressEntity.seq.eq(addressDto.getSeq()));
	    }
		if (addressDto.getAddressName() != null) {
	    	wheres.and(QAddressEntity.addressEntity.addressName.eq(addressDto.getAddressName()));
	    }
		if (addressDto.getSidoCode() != null) {
	    	wheres.and(QAddressEntity.addressEntity.sidoCode.eq(addressDto.getSidoCode()));
	    }
		if (addressDto.getGugunCode() != null) {
	    	wheres.and(QAddressEntity.addressEntity.gugunCode.eq(addressDto.getGugunCode()));
	    }
		if (addressDto.getDongCode() != null) {
	    	wheres.and(QAddressEntity.addressEntity.dongCode.eq(addressDto.getDongCode()));
	    }
		if (addressDto.getRiCode() != null) {
	    	wheres.and(QAddressEntity.addressEntity.riCode.eq(addressDto.getRiCode()));
	    }
		
		return wheres;
	}
}
