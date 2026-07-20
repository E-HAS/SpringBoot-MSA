package com.msa.auth.address.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAddressEntity is a Querydsl query type for AddressEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAddressEntity extends EntityPathBase<AddressEntity> {

    private static final long serialVersionUID = -1557732884L;

    public static final QAddressEntity addressEntity = new QAddressEntity("addressEntity");

    public final StringPath addressName = createString("addressName");

    public final NumberPath<Integer> dongCode = createNumber("dongCode", Integer.class);

    public final NumberPath<Integer> gugunCode = createNumber("gugunCode", Integer.class);

    public final NumberPath<Integer> riCode = createNumber("riCode", Integer.class);

    public final NumberPath<Integer> seq = createNumber("seq", Integer.class);

    public final NumberPath<Integer> sidoCode = createNumber("sidoCode", Integer.class);

    public QAddressEntity(String variable) {
        super(AddressEntity.class, forVariable(variable));
    }

    public QAddressEntity(Path<? extends AddressEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAddressEntity(PathMetadata metadata) {
        super(AddressEntity.class, metadata);
    }

}

