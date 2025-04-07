package com.app.bdc_backend.elasticsearch.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.aggregations.*;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class ESShopOrderRepository {

    private final ElasticsearchClient client;

    public Map<Integer, Integer> countShopOrdersByStatus(String shopId) throws IOException {
        Aggregation agg = AggregationBuilders.terms(t ->
                t.field("status"));
        Query nativeQuery = Query.of(q -> q.term(
                        t -> t.field("shopId").value(shopId)));
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("shop_orders")
                .query(nativeQuery)
                .aggregations(Map.of("status_count", agg));
        SearchRequest request = searchBuilder.build();
        System.out.println( "Query: " + request);
        SearchResponse<ObjectNode> result = client.search(request, ObjectNode.class);
        System.out.println("Result:: " + result);
        Aggregate statusAgg = result.aggregations().get("status_count");
        Map<Integer, Integer> statusCount = new HashMap<>();

        // kiểm tra lterm vì key là số nguyên (lterm = long term, sterm - string, ...)
        if (statusAgg.isLterms()) {
            List<LongTermsBucket> buckets = statusAgg.lterms().buckets().array();
            for (LongTermsBucket bucket : buckets) {
                int status = Integer.parseInt(String.valueOf(bucket.key()));
                int count = (int) bucket.docCount();
                log.info("Status: {}, Count: {}", status, count);
                statusCount.put(status, count);
            }
        }
        return statusCount;
    }

}
