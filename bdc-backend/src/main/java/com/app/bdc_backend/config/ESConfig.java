package com.app.bdc_backend.config;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ESConfig {

    final RestClientBuilder restClientbuilder = RestClient.builder(new HttpHost("localhost", 9200, "http"));

    @Bean
    public ElasticsearchClient esTransport() {
        System.out.println("Config ES");
        ObjectMapper mapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
        RestClient restClient = restClientbuilder.build();

        ElasticsearchTransport transport = new RestClientTransport(restClient,
                new JacksonJsonpMapper(mapper));
        return new ElasticsearchClient(transport);
    }

}
