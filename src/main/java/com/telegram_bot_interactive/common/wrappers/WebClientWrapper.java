
package com.telegram_bot_interactive.common.wrappers;

import com.telegram_bot_interactive.configuration.ApplicationConfiguration;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunctions;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import javax.net.ssl.SSLException;
import java.time.Duration;

@Getter
@Component
public class WebClientWrapper {

    private WebClient.Builder webClientBuilder;

    @Autowired
    private ApplicationConfiguration appSetting;

    @Setter
    private int REQUEST_TIME_OUT;

    @PostConstruct
    public void init() throws SSLException {

        SslContext sslContext;
        try {
            sslContext = SslContextBuilder
                .forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE) // Trust all certs
                .build();
        } catch (SSLException e) {
            sslContext = SslContextBuilder
                .forClient()
                .build();
        }

        SslContext finalSslContext = sslContext;
        var httpClient = HttpClient.create()
            .secure(ssl -> ssl.sslContext(finalSslContext))
            .responseTimeout(Duration.ofMillis(appSetting.getGlobalRequestTimeoutMillisecond()));

        webClientBuilder =  WebClient
            .builder()
            .clientConnector(new ReactorClientHttpConnector(httpClient));
    }

    public WebClientWrapper useBasicAuthentication(String username, String password) {
        webClientBuilder
            .filter(ExchangeFilterFunctions.basicAuthentication(username, password));
        return this;
    }


    public ResponseEntity<?> getSync(String url) {

        return  webClientBuilder
            .build()
            .get()
            .uri(url)
                .accept(MediaType.APPLICATION_JSON)
            .retrieve()
            .toEntity(Object.class)
            .timeout(Duration.ofMillis(this.getRequestTimeout()))
            .onErrorResume(err -> {
                return Mono.error(err);
            })
            .block();
    }

    public Mono<ResponseEntity<String>> getAsync( String url) {
        return  webClientBuilder
            .build()
            .get()
            .uri(url)
            .retrieve()
            .toEntity(String.class)
            .timeout(Duration.ofMillis(this.getRequestTimeout()))
            .onErrorResume(err -> {
                //super.logError(err, "url[%s]; text[%s]".formatted(url, err.getMessage()));
                return Mono.error(err);
            })
        ;
    }

    public ResponseEntity<String> post(String url, Object payload) {

        return  webClientBuilder
            .build()
            .post()
            .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .toEntity(String.class)
            .timeout(Duration.ofMillis(this.getRequestTimeout()))
            .onErrorResume(err -> {
                //super.logError(err, "url[%s]; text[%s]".formatted(url, err.getMessage()));
                return Mono.error(err);
            })
            .block();
    }

    public Mono<ResponseEntity<String>> postJsonAsync(String url, Object payload) {
        return  webClientBuilder
            .build()
            .post()
            .uri(url)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(payload)
            .retrieve()
            .toEntity(String.class)
            .timeout(Duration.ofMillis(this.getRequestTimeout()))
            .onErrorResume(err -> {
                //super.logError(err, "url[%s]; text[%s]".formatted(url, err.getMessage()));
                return Mono.error(err);
            });
    }

    public Mono<ResponseEntity<String>> postXmlAsync(String url, Object payload) {
        return  webClientBuilder
            .build()
            .post()
            .uri(url)
            .contentType(MediaType.APPLICATION_XML)
            .bodyValue(payload)
            .retrieve()
            .toEntity(String.class)
            .timeout(Duration.ofMillis(this.getRequestTimeout()))
            .onErrorResume(err -> {
                //super.logError(err, "url[%s]; text[%s]".formatted(url, err.getMessage()));
                return Mono.error(err);
            });
    }

    private int getRequestTimeout() {
        return REQUEST_TIME_OUT < 1
            ? appSetting.globalRequestTimeoutMillisecond
            : REQUEST_TIME_OUT;
    }


}
