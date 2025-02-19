package com.farmorai.backend.controller;

import com.farmorai.backend.service.NaverPayReserveRequest;
import com.farmorai.backend.service.NaverPayReserveResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

@Controller
@RequestMapping("/payment")
public class PaymentController {

    @Value("${naver.pay.client-id}")
    private String naverPayClientId;

    @Value("${naver.pay.client-secret}")
    private String naverPayClientSecret;

    @Value("${naver.pay.chain-id}")
    private String naverPayChainId;

    @Autowired
    private RestTemplate restTemplate;


    @PostMapping("/naverPay")
    public ResponseEntity<?> naverPayReserve(@RequestBody Map<String, String> reqBody) {
        String subsPlan = reqBody.get("subsPlan");
        String subsPrice = reqBody.get("subsPrice");

        NaverPayReserveRequest payInfo = NaverPayReserveRequest.builder()
                .merchantPayKey(UUID.randomUUID().toString())
                .productName(subsPlan)
                .productCount(1)
                .totalPayAmount(Integer.parseInt(subsPrice))
                .taxScopeAmount(Integer.parseInt(subsPrice))
                .taxExScopeAmount(0)
                .returnUrl(String.format("https://localhost:6060/api/payment/resultPay?subsPlan=%s&subsPrice=%s", subsPlan, subsPrice))
                .build();

        String url = "https://dev-pub.apis.naver.com/naverpay-partner/naverpay/payments/v2/reserve";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-Naver-Client-Id", naverPayClientId);
        headers.set("X-Naver-Client-Secret", naverPayClientSecret);
        headers.set("X-NaverPay-Chain-Id", naverPayChainId);
        headers.set("X-NaverPay-Idempotency-Key", UUID.randomUUID().toString());

        HttpEntity<NaverPayReserveRequest> entity = new HttpEntity<>(payInfo, headers);

        try {
            ResponseEntity<NaverPayReserveResponse> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    NaverPayReserveResponse.class
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(response.getBody());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing payment: " + e.getMessage());
        }
    }
}
