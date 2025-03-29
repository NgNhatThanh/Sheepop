package com.app.bdc_backend.elasticsearch.dao;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.SortOrder;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.app.bdc_backend.elasticsearch.model.ESProduct;
import com.app.bdc_backend.model.dto.request.ProductSearchFilters;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQueryBuilder;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

@Repository
@RequiredArgsConstructor
public class ESProductRepository{

    private final ElasticsearchClient client;

    public Page<ESProduct> homepageSearch(String keyword, Pageable pageable, ProductSearchFilters filters) throws IOException {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("products")
                .query(q -> q
                    .bool(b -> {
                        BoolQuery.Builder boolBuilder = commonBoolQueryBuilder();
                        if(keyword != null) {
                            boolBuilder.filter(m -> m.match(t ->
                                    t.field("name").query(keyword).fuzziness("1")));
                        }
                        if (!filters.getCategoryIds().isEmpty()) {
                            boolBuilder.filter(f -> f.terms(t ->
                                    t.field("categoryId").terms(ts ->
                                            ts.value(filters.getCategoryIds().stream().map(
                                                    FieldValue::of).toList()))));
                        }

                        if (!filters.getLocations().isEmpty()) {
                            boolBuilder.filter(f -> f.terms(t ->
                                    t.field("location.keyword").terms(ts ->
                                            ts.value(filters.getLocations().stream().map(
                                                    FieldValue::of).toList()))));
                        }

                        if (filters.getMinPrice() != null) {
                            boolBuilder.filter(f -> f.range(m ->
                                    m.number(v ->
                                            v.field("price").gte(Double.valueOf(filters.getMinPrice())))));
                        }
                        if (filters.getMaxPrice() != null) {
                            boolBuilder.filter(f -> f.range(m ->
                                    m.number(v ->
                                            v.field("price").lte(Double.valueOf(filters.getMaxPrice())))));
                        }
                        if (filters.getMinRating() != null) {
                            boolBuilder.filter(f -> f.range(m ->
                                    m.number(v ->
                                            v.field("averageRating").gte(Double.valueOf(filters.getMinRating())))));
                        }
                        if(filters.getShopId() != null){
                            boolBuilder.must(m -> m.term(t ->
                                    t.field("shopId").value(filters.getShopId())));
                        }

                        return boolBuilder;
                    })
                );
        return getEsProducts(pageable, searchBuilder);
    }

    public Page<ESProduct> getShopProducts(String shopId, String categoryId, Pageable pageable) throws IOException {
        SearchRequest.Builder searchBuilder = new SearchRequest.Builder()
                .index("products")
                .query(q -> q
                        .bool(b -> {
                            BoolQuery.Builder boolBuilder = commonBoolQueryBuilder();
                            boolBuilder.must(m -> m.term(t ->
                                    t.field("shopId").value(shopId)));
                            if (categoryId != null && !categoryId.isEmpty()) {
                                boolBuilder.must(m -> m.term(t ->
                                        t.field("categoryId").value(categoryId)));
                            }
                            return boolBuilder;
                        })
                );
        return getEsProducts(pageable, searchBuilder);
    }

    private BoolQuery.Builder commonBoolQueryBuilder(){
        BoolQuery.Builder boolBuilder = new BoolQuery.Builder();
        boolBuilder.must(m -> m.term(t ->
                t.field("deleted").value(false)));
        boolBuilder.must(m -> m.term(t ->
                t.field("visible").value(true)));
        boolBuilder.must(m -> m.term(t ->
                t.field("restricted").value(false)));
        return boolBuilder;
    }

    private Page<ESProduct> getEsProducts(Pageable pageable, SearchRequest.Builder searchBuilder) throws IOException {
        Sort sort = pageable.getSort();
        String sortBy = sort.get().toList().get(0).getProperty();
        Sort.Direction direction = sort.get().toList().get(0).getDirection();
        searchBuilder.sort(s ->
                s.field(f ->
                        f.field(sortBy)
                                .order(direction == Sort.Direction.DESC ? SortOrder.Desc : SortOrder.Asc)));
        searchBuilder.sort(s ->
                s.field(f ->
                        f.field("averageRating")
                                .order(SortOrder.Desc)));
        searchBuilder.from((int) pageable.getOffset()).size(pageable.getPageSize());
        SearchRequest rq = searchBuilder.build();
        SearchResponse<ObjectNode> response = client.search(rq, ObjectNode.class);
        NativeQueryBuilder nativeQueryBuilder = new NativeQueryBuilder();
        nativeQueryBuilder.withQuery(Objects.requireNonNull(rq.query()));
        System.out.println(nativeQueryBuilder.getQuery());
        List<ObjectNode> nodes = response.hits().hits().stream()
                .map(hit -> {
                    ObjectNode node = hit.source();
                    node.put("id", hit.id());
                    return node;
                }).toList();
        List<ESProduct> products = nodes.stream().map(this::toESProduct).toList();
        return new PageImpl<>(products, pageable, response.hits().total().value());
    }

    private ESProduct toESProduct(ObjectNode node) {
        ESProduct product = new ESProduct();
        product.setId(node.get("id").asText());
        product.setName(node.get("name").textValue());
        product.setSold(node.get("sold").intValue());
        product.setPrice(node.get("price").longValue());
        product.setAverageRating(node.get("averageRating").doubleValue());
        product.setThumbnailUrl(node.get("thumbnailUrl").textValue());
        product.setLocation(node.get("location").textValue());
        return product;
    }

}
