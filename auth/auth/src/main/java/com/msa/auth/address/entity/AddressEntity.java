package com.msa.auth.address.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name ="address")
public class AddressEntity {
    @Id
    private Integer seq;
    private String addressName;
    
    private Integer sidoCode;
    private Integer gugunCode;
    private Integer dongCode;
    private Integer riCode;
    
}
