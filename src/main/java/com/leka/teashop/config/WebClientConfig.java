package com.leka.teashop.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import io.netty.resolver.DefaultAddressResolverGroup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.util.concurrent.TimeUnit;

@Configuration
public class WebClientConfig {
    @Value("${media.webclient.base-url}")
    private String MEDIA_BASE_URL;
    @Value("${order.webclient.base-url}")
    private String ORDER_BASE_URL;
    @Value("${general.webclient.timeout}")
    public int TIMEOUT;

    @LoadBalanced
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean(name = "mediaWebClient")
    public WebClient mediaWebClientWithTimeout() {
        final int memorySize = 20 * 1024 * 1024; //20MB
        final ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(memorySize))
                .build();
        return webClientBuilder()
                .baseUrl(MEDIA_BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .exchangeStrategies(strategies)
                .build();
    }

    @Bean(name = "orderWebClient")
    public WebClient orderWebClientWithTimeout() {
        return webClientBuilder()
                .baseUrl(ORDER_BASE_URL)
                .clientConnector(new ReactorClientHttpConnector(getHttpClient()))
                .build();
    }

    private HttpClient getHttpClient() {
        return HttpClient.create()
                .resolver(DefaultAddressResolverGroup.INSTANCE)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, TIMEOUT)
                .doOnConnected(conn -> {
                    conn.addHandlerLast(new ReadTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                    conn.addHandlerLast(new WriteTimeoutHandler(TIMEOUT, TimeUnit.MILLISECONDS));
                });
    }
}
