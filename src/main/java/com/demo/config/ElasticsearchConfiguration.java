package com.demo.config;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.StatusLine;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

@Configuration
public class ElasticsearchConfiguration {

    @Value("${es_hostname}")
    private String esHostname;

    @Value("${es_port}")
    private String esPort;

    @Value("${es_scheme}")
    private String esScheme;

    @Bean
    public RestClient getRestClient() {
        List<HttpHost> httpHostList = new ArrayList<>();
        String[] hostNames = esHostname.split(";");
        String[] ports = esPort.split(";");
        String[] schemes = esScheme.split(";");
        for (int i = 0; i < hostNames.length; i++) {
            httpHostList.add(new HttpHost(hostNames[i], Integer.valueOf(ports[i]), schemes[i]));
        }
        HttpHost[] httpHosts = new HttpHost[httpHostList.size()];
        return  RestClient.builder(httpHostList.toArray(httpHosts)).build();
    }

}
