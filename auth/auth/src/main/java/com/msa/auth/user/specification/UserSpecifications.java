package com.msa.auth.user.specification;

import org.springframework.data.jpa.domain.Specification;

import com.msa.auth.user.entity.UserEntity;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;


public class UserSpecifications {
    public static Specification<UserEntity> findWith(
            Integer status,
            Integer seq,
            String id,
            String name,
            String stDt,
            String enDt
    ) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), status));
            }

            if (seq != null) {
                predicates.add(criteriaBuilder.equal(root.get("seq"), seq));
            }

            if (id != null && !id.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("id"), id ));
            }

            if (name != null && !name.isEmpty()) {
                predicates.add(criteriaBuilder.like(root.get("name"), "%" + name + "%"));
            }

            if (stDt != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("registeredDate"), stDt));
            }

            if (enDt != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("registeredDate"), enDt));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}