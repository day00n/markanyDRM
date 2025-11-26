package com.stove.drm.adapter.core.config;

import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiGroupConfig {

    @Bean
    public GroupedOpenApi v1Api() {
        return GroupedOpenApi.builder()
                .group("v1")
                .pathsToMatch("/v1/**")
                .addOpenApiCustomizer(openApi -> {
                    openApi.setServers( List.of(
                            new Server().url("https://dooray-drm.spay.sginfra.net")
                            ,new Server().url("http://localhost:9090")
                            ,new Server().url("http://172.26.102.72:9090")
                            )
                    );
                }).build();
    }

//    @Bean
//    public GroupedOpenApi adminApi() {
//        return GroupedOpenApi.builder()
//                .group("admin")
//                .pathsToMatch("/admin/**")
//                .build();
//    }
}