package com.farmorai.backend.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

@Document(indexName = "farmdb_product", createIndex = true)
@Data
@Builder
@Mapping(mappingPath = "elasticsearch/mappings.json")
@Setting(settingPath = "elasticsearch/settings.json")
public class ProductDoc {

    @Id
    private Long productId;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String name;

    @Field(type = FieldType.Text, analyzer = "korean")
    private String description;

    private String variety;  // 품종 (정확한 검색)

    private double price;  // 가격

    private int stock;  // 재고



}
