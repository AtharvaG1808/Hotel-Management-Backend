// File: src/main/java/cg/dfs/hotel/service/PaymentService.java
package cg.dfs.hotel.service;

import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Service
public class PaymentService {

    private final RazorpayClient razorpayClient;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${razorpay.keyId}")
    private String keyId;

    @Value("${razorpay.keySecret}")
    private String keySecret;

    public PaymentService(RazorpayClient razorpayClient) {
        this.razorpayClient = razorpayClient;
    }

    public Order createOrder(int amountInPaise, String currency, String receipt) throws RazorpayException {
        JSONObject request = new JSONObject();
        request.put("amount", amountInPaise);
        request.put("currency", currency);
        request.put("receipt", receipt);
        request.put("payment_capture", 1);
        return razorpayClient.orders.create(request);
    }

    public String createPaymentLink(int amountInPaise, String description) {
        String api = "https://api.razorpay.com/v1/payment_links";

        JSONObject payload = new JSONObject();
        payload.put("amount", amountInPaise);
        payload.put("currency", "INR");
        payload.put("description", description);
        // minimal customer info; adjust as needed
        JSONObject customer = new JSONObject();
        customer.put("name", "Customer");
        payload.put("customer", customer);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        String auth = keyId + ":" + keySecret;
        String encoded = Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8));
        headers.set("Authorization", "Basic " + encoded);

        HttpEntity<String> entity = new HttpEntity<>(payload.toString(), headers);
        ResponseEntity<String> resp = restTemplate.postForEntity(api, entity, String.class);

        if (resp.getStatusCode().is2xxSuccessful() && resp.getBody() != null) {
            JSONObject body = new JSONObject(resp.getBody());
            // return short_url if available, otherwise long_url
            return body.optString("short_url", body.optString("long_url", null));
        }

        throw new RuntimeException("Failed to create payment link: " + resp.getStatusCode().value());
    }
}