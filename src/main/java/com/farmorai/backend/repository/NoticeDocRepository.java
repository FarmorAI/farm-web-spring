package com.farmorai.backend.repository;

import com.farmorai.backend.domain.ProductDoc;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.query.Param;


import java.util.List;

public interface NoticeDocRepository{

    @Query("""
        {
            "bool": {
                "should": [
                    {
                        "match": {
                            "title": "?0"
                        }
                    },
                    {
                        "match": {
                            "content": "?0"
                        }
                    }
                ]
            }
        }
        """)
    List<ProductDoc> searchByKeyword(@Param("keyword") String keyword);
}
