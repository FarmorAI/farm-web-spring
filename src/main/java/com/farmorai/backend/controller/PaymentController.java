package com.farmorai.backend.controller;

import com.farmorai.backend.dto.NaverPayInfoDto;
import com.farmorai.backend.dto.PaymentDto;
import com.farmorai.backend.dto.SubsDto;
import com.farmorai.backend.service.PaymentService;
import com.farmorai.backend.service.SubsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {
    private WebClient webClient;
    private final PaymentService paymentService;
    private final SubsService subsService;

    @Autowired
    public PaymentController(WebClient.Builder webClientBuilder, PaymentService paymentService, SubsService subsService) {
        this.webClient = webClientBuilder.baseUrl("https://dev-pub.apis.naver.com").build();
        this.paymentService = paymentService;
        this.subsService = subsService;
    }

    @Value("${naver.pay.client-id}")
    private String naverPayClientId;

    @Value("${naver.pay.client-secret}")
    private String naverPayClientSecret;

    @Value("${naver.pay.chain-id}")
    private String naverPayChainId;


    @PostMapping("/naverpay")
    public Mono<ResponseEntity<?>> naverPayReserve(@RequestBody Map<String, String> reqBody) {
        String subsPlan = reqBody.get("subsPlan");
        String subsPrice = reqBody.get("subsPrice");

        // Validate Params
        if (subsPlan == null || subsPrice == null) {
            return Mono.fromSupplier(() -> ResponseEntity.badRequest().body("Missing required parameters"));
        }

        // PaymentDto 생성 및 레코드 DB 저장
        PaymentDto paymentDto = paymentService.insertPayment("admin1@example.com", subsPlan, subsPrice);
        String pid = "pid" + paymentDto.getPaymentId();
        String encodedPid = Base64.getEncoder().encodeToString(pid.getBytes(StandardCharsets.UTF_8));

        // NaverPay API 요청 정보
        NaverPayInfoDto payInfo = NaverPayInfoDto.builder()
                .merchantPayKey(UUID.randomUUID().toString())
                .productName(subsPlan)
                .productCount(1)
                .totalPayAmount(Integer.parseInt(subsPrice))
                .taxScopeAmount(Integer.parseInt(subsPrice))
                .taxExScopeAmount(0)
                .returnUrl(String.format("http://localhost:6060/payment/result?subsPlan=%s&subsPrice=%s&epd=%s", subsPlan, subsPrice, encodedPid))
                .build();

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
            @RequestParam(required = false) String epd,
            HttpServletResponse resp
    ) throws IOException {

        // encodedPid decoding
        String decodedpid = new String(Base64.getDecoder().decode(epd));
        Long pid = Long.parseLong(decodedpid.substring(3));

        // create SubDto and update paymentDto
        PaymentDto paymentDto = paymentService.getPaymentById(pid);
        SubsDto subsDto = subsService.insertSubs(paymentDto.getMemberId(), paymentDto.getPlanId());
        paymentService.updatePayment(paymentDto, paymentId, subsDto.getSubsId());

        // string encoding
        String encodedSubsPlan = URLEncoder.encode(subsPlan, StandardCharsets.UTF_8);
        String redirectUrl = "http://localhost:3030/payment/result?";
        String params = "&resultCode=" + resultCode + "&paymentId=" + paymentId + "&subsPlan=" + encodedSubsPlan + "&subsPrice=" + subsPrice;

        if ("Success".equals(resultCode)) {
            // 결과 코드가 Success이면 성공 페이지로 리다이렉트
            resp.sendRedirect(redirectUrl + params);
        } else {
            // 실패한 경우 실패 페이지로 리다이렉트
            resp.sendRedirect(redirectUrl + params);
        }
    }


    @PostMapping("/cancel")
    public Map<String, String> naverPayCancel(@RequestBody Map<String, String> reqBody) {
        String paymentId = reqBody.get("paymentId");

        subsService.cancelSubs(paymentId);
        paymentService.refundPayment(paymentId);
        return Map.of("result", paymentId);
    }
}