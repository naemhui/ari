package com.ccc.ari.aggregation.application.repository;

import com.ccc.ari.aggregation.infrastructure.entity.AggregatedDataEntity;

import java.util.List;

public interface AggregatedDataRepository {

    List<AggregatedDataEntity> findAll();

    AggregatedDataEntity save(AggregatedDataEntity aggregatedDataEntity);
}
