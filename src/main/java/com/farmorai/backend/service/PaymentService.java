package com.farmorai.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class PaymentService {

    @Value("${naver.pay.client.id}")
    private String clientId;

    @Value("${naver.pay.client.secret}")
    private String clientSecret;

    @Value("${naver.pay.chain.id}")
    private String chainId;

    // WebClient는 비동기 방식으로 HTTP 요청을 처리하는 Spring WebFlux의 HTTP 클라이언트
    private final WebClient webClient;

    public PaymentService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://dev-pub.apis.naver.com").build();
    }



    public Mono<NaverPayReserveResponse> reservePayment(NaverPayReserveRequest payInfo) {
        return webClient.post()
                .uri("/naverpay-partner/naverpay/payments/v2/reserve")
                .header("X-Naver-Client-Id", clientId)
                .header("X-Naver-Client-Secret", clientSecret)
                .header("X-NaverPay-Chain-Id", chainId)
                .header("X-NaverPay-Idempotency-Key", UUID.randomUUID().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(payInfo)
                .retrieve()
                .bodyToMono(NaverPayReserveResponse.class);
    }
}