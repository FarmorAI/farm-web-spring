//package com.farmorai.backend.repository;
//
//import com.farmorai.backend.domain.ProductDoc;
//import org.springframework.data.elasticsearch.annotations.Query;
//import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
//import org.springframework.data.repository.query.Param;
//
//
//import java.util.List;
//
//public interface ProductDocRepository extends ElasticsearchRepository<ProductDoc, String> {
//
//    @Query("""
//        {
//            "bool": {
//                "must": [
//                    { "match_all": {} }
//                ]
//            }
//        }
//        """)
//    List<ProductDoc> getProductList();
//
//    @Query("{\"bool\": {\"should\": [{\"match\": {\"name\": \"?0\"}}, {\"match\": {\"description\": \"?0\"}}]}}")
//    List<ProductDoc> searchByKeyword(String keyword);
//}
