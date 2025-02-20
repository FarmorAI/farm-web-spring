package com.farmorai.backend.controller;

import com.farmorai.backend.dto.NaverPayInfoDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/payment")
public class PaymentController {

    @Value("${naver.pay.client-id}")
    private String naverPayClientId;

    @Value("${naver.pay.client-secret}")
    private String naverPayClientSecret;

    @Value("${naver.pay.chain-id}")
    private String naverPayChainId;

    private final WebClient webClient;

    public PaymentController(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://dev-pub.apis.naver.com").build();
    }

    @PostMapping("/naverpay")
    public Mono<ResponseEntity<?>> naverPayReserve(@RequestBody Map<String, String> reqBody) {
        String subsPlan = reqBody.get("subsPlan");
        String subsPrice = reqBody.get("subsPrice");

        // Validate inputs
        if (subsPlan == null || subsPrice == null) {
            return Mono.fromSupplier(() -> ResponseEntity.badRequest().body("Missing required parameters"));
        }

        NaverPayInfoDto payInfo = NaverPayInfoDto.builder()
                .merchantPayKey(UUID.randomUUID().toString())
                .productName(subsPlan)
                .productCount(1)
                .totalPayAmount(Integer.parseInt(subsPrice))
                .taxScopeAmount(Integer.parseInt(subsPrice))
                .taxExScopeAmount(0)
                .returnUrl(String.format("http://localhost:6060/payment/result?subsPlan=%s&subsPrice=%s", subsPlan, subsPrice))
                .build();

        log.debug("payInfo : {}", payInfo);

        return webClient.post()
                .uri("/naverpay-partner/naverpay/payments/v2/reserve")
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Naver-Client-Id", naverPayClientId)
                .header("X-Naver-Client-Secret", naverPayClientSecret)
                .header("X-NaverPay-Chain-Id", naverPayChainId)
                .header("X-NaverPay-Idempotency-Key", UUID.randomUUID().toString())
                .body(Mono.just(payInfo), NaverPayInfoDto.class)
                .retrieve()
                .bodyToMono(Map.class)
                .doOnTerminate(() -> log.debug("Request completed"))
                .doOnSuccess(response -> log.debug("Response received: {}", response))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }


    @GetMapping("/result")
    public void naverPayResult(
            @RequestParam String resultCode,
            @RequestParam(required = false) String paymentId,
            @RequestParam String subsPlan,
            @RequestParam String subsPrice,
            HttpServletResponse resp
    ) throws IOException {

        // URL 인코딩 처리
        String encodedSubsPlan = URLEncoder.encode(subsPlan, StandardCharsets.UTF_8);
        String redirectUrl = "http://localhost:3030/payment/result?";
        String params = "&resultCode=" + resultCode + "&paymentId=" + paymentId + "&subsPlan=" + encodedSubsPlan + "&subsPrice=" + subsPrice;

        // 결과 코드가 Success이면 성공 페이지로 리다이렉트
        if ("Success".equals(resultCode)) {
            resp.sendRedirect(redirectUrl + params);
        } else {
            // 실패한 경우 실패 페이지로 리다이렉트
            resp.sendRedirect(redirectUrl + params);
        }
    }


    @PostMapping("/cancel")
    public Map<String, String> naverPayCancel(@RequestBody Map<String, String> reqBody) {
        String paymentId = reqBody.get("paymentId");

        log.debug("cancel paymentId : {}", paymentId);

        return Map.of("result", paymentId);
    }
}