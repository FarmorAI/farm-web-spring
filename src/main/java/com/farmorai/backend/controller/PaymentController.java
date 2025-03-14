package com.farmorai.backend.controller;

import com.farmorai.backend.dto.NaverPayInfoDto;
import com.farmorai.backend.dto.PaymentDto;
import com.farmorai.backend.dto.PaymentSubsDto;
import com.farmorai.backend.dto.SubsDto;
import com.farmorai.backend.securityFilter.CustomUserDetails;
import com.farmorai.backend.service.CartService;
import com.farmorai.backend.service.OrderService;
import com.farmorai.backend.service.PaymentService;
import com.farmorai.backend.service.SubsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/payment")
public class PaymentController {
    private WebClient webClient;
    private final PaymentService paymentService;
    private final SubsService subsService;
    private OrderService orderService;
    private CartService cartService;

    @Autowired
    public PaymentController(WebClient.Builder webClientBuilder, PaymentService paymentService, SubsService subsService, OrderService orderService, CartService cartService) {
        this.webClient = webClientBuilder.baseUrl("https://dev-pub.apis.naver.com").build();
        this.paymentService = paymentService;
        this.subsService = subsService;
        this.orderService = orderService;
        this.cartService = cartService;
    }

    @Value("${naver.pay.client-id}")
    private String naverPayClientId;

    @Value("${naver.pay.client-secret}")
    private String naverPayClientSecret;

    @Value("${naver.pay.chain-id}")
    private String naverPayChainId;


    // 구독 결제 정보 조회
    @GetMapping("/subsInfo")
    public ResponseEntity<PaymentSubsDto> subscriptions(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        PaymentSubsDto paymentSubsInfo = paymentService.getPaymentInfo(userDetails.getMemberId());
        return ResponseEntity.ok().body(paymentSubsInfo);
    };


    // 기존 단일 상품 결제
    @PostMapping("/naverpay")
    public Mono<ResponseEntity<?>> naverPayReserve(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, String> reqBody
    ) {
        log.debug(userDetails.toString());
        String subsPlan = reqBody.get("subsPlan");
        String subsPrice = reqBody.get("subsPrice");

        // Validate Params
        if (subsPlan == null || subsPrice == null) {
            return Mono.fromSupplier(() -> ResponseEntity.badRequest().body("Missing required parameters"));
        }

        // PaymentDto 생성 및 레코드 DB 저장
        PaymentDto paymentDto = paymentService.insertPayment(userDetails.getMemberId(), subsPlan, subsPrice);
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
                .returnUrl(String.format("http://localhost:6060/api/payment/result?subsPlan=%s&subsPrice=%s&epd=%s", subsPlan, subsPrice, encodedPid))
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


    // 장바구니 결제 추가
    @PostMapping("/naverpay/cart")
    public Mono<ResponseEntity<?>> naverPayCartReserve(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestBody Map<String, Object> request
    ) {
        log.info("Request received: {}", request);
        List<Map<String, Object>> items = (List<Map<String, Object>>) request.get("items");
        log.info("Items received: {}", items);
        String orderNumber = (String) request.get("orderNumber");

        if (items == null || items.isEmpty()) {
            return Mono.fromSupplier(() -> ResponseEntity.badRequest().body("No cart items provided"));
        }

        // 총액 계산
        int totalAmount = items.stream()
                .mapToInt(item -> ((Number) item.get("price")).intValue() * ((Number) item.get("quantity")).intValue())
                .sum();

        // PaymentDto 생성 (장바구니는 subsPlan 대신 "Cart"로 고정)
        PaymentDto paymentDto = paymentService.insertPayment(userDetails.getMemberId(), "Cart", String.valueOf(totalAmount));
        String pid = "pid" + paymentDto.getPaymentId();
        String encodedPid = Base64.getEncoder().encodeToString(pid.getBytes(StandardCharsets.UTF_8));

        // NaverPay API 요청 정보
        NaverPayInfoDto payInfo = NaverPayInfoDto.builder()
                .merchantPayKey(UUID.randomUUID().toString())
                .productName("장바구니 결제 (" + items.size() + "건)")
                .productCount(items.size())
                .totalPayAmount(totalAmount)
                .taxScopeAmount(totalAmount)
                .taxExScopeAmount(0)
                .returnUrl(String.format("http://localhost:6060/api/payment/result?orderNumber=%s&subsPlan=Cart&subsPrice=%d&epd=%s", orderNumber,totalAmount, encodedPid))
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
                .doOnTerminate(() -> log.debug("Cart payment request completed"))
                .doOnSuccess(response -> log.debug("Cart payment response received: {}", response))
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @GetMapping("/result")
    public void naverPayResult(
            @RequestParam String resultCode,
            @RequestParam(required = false) String paymentId,
            @RequestParam String subsPlan,
            @RequestParam String subsPrice,
            @RequestParam(required = false) String epd,
            @RequestParam(required = false) String orderNumber,
            HttpServletResponse resp
    ) throws IOException {
        // encodedPid decoding
        String decodedPid = new String(Base64.getDecoder().decode(epd));
        Long pid = Long.parseLong(decodedPid.substring(3));

        // PaymentDto 조회 및 업데이트
        PaymentDto paymentDto = paymentService.getPaymentById(pid);
        if (paymentDto.getPlanId() < 3) {
            SubsDto subsDto = subsService.insertSubs(paymentDto.getMemberId(), paymentDto.getPlanId());
            paymentService.updatePayment(paymentDto, paymentId, subsDto.getSubsId());
        } else {
            paymentService.updatePayment(paymentDto, paymentId, null);
        }

        // string encoding
        String encodedSubsPlan = URLEncoder.encode(subsPlan, StandardCharsets.UTF_8);
        String redirectUrl;
        if("Cart".equals(subsPlan)) {
        // cartService.deleteCartItem(subsDto.getMemberId(), );
            orderService.updateOrderStatus(orderNumber,paymentId);
            redirectUrl = "http://localhost:3030/cart/payment/result?orderNumber=" + orderNumber+"&";
        }
        else {
            redirectUrl = "http://localhost:3030/payment/result?";
        }
        String params = "resultCode=" + resultCode + "&paymentId=" + paymentId + "&subsPlan=" + encodedSubsPlan + "&subsPrice=" + subsPrice;

        if ("Success".equals(resultCode)) {
            resp.sendRedirect(redirectUrl + params);
        } else {
            resp.sendRedirect(redirectUrl + params);
        }
    }

    @PostMapping("/cancel")
    public Map<String, String> naverPayCancel(@RequestBody Map<String, String> reqBody) {
        String token = reqBody.get("token");
        log.debug(token);
        subsService.cancelSubs(token);
        paymentService.refundPayment(token);
        return Map.of("result", token);
    }
}