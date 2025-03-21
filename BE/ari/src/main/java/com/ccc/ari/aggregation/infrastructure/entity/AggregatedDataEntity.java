package com.ccc.ari.aggregation.infrastructure.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "aggregated_datas")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class AggregatedDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "aggregated_data_id")
    private Integer aggregatedDataId;

    @Column(nullable = false, length = 100)
    private String cid;
}
