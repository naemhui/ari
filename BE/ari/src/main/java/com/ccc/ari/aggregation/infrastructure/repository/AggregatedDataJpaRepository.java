package com.ccc.ari.aggregation.infrastructure.repository;

import com.ccc.ari.aggregation.infrastructure.entity.AggregatedDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AggregatedDataJpaRepository extends JpaRepository<AggregatedDataEntity, Integer> {
}
