package com.farmorai.backend.domain;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = "farmdb_product", createIndex = true)
@Data
@Builder
@Mapping(mappingPath = "elasticsearch/mappings.json")
@Setting(settingPath = "elasticsearch/settings.json")
public class ProductDoc {

    @Id
    private Long productId;
    private String name;
    private String description;


}
