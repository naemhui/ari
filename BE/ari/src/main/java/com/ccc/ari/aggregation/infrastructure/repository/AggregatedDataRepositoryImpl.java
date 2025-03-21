package com.ccc.ari.aggregation.infrastructure.repository;

import com.ccc.ari.aggregation.application.repository.AggregatedDataRepository;
import com.ccc.ari.aggregation.infrastructure.entity.AggregatedDataEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class AggregatedDataRepositoryImpl implements AggregatedDataRepository {

    private final AggregatedDataJpaRepository aggregatedDataJpaRepository;

    @Override
    public List<AggregatedDataEntity> findAll() {
        return aggregatedDataJpaRepository.findAll();
    }

    @Override
    public AggregatedDataEntity save(AggregatedDataEntity aggregatedDataEntity) {
        return aggregatedDataJpaRepository.save(aggregatedDataEntity);
    }
}
