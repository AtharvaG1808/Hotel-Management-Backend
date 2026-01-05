// java
    // File: `src/main/java/cg/dfs/hotel/controller/PaymentController.java`
    package cg.dfs.hotel.controller;

    import com.razorpay.Order;
    import com.razorpay.RazorpayException;
    import cg.dfs.hotel.service.PaymentService;
    import org.json.JSONObject;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.util.HashMap;
    import java.util.Map;


    @CrossOrigin(origins = "http://localhost:3000")
    @RestController
    @RequestMapping("/payments")
    public class PaymentController {

        private final PaymentService paymentService;

        @Value("${razorpay.keyId}")
        private String keyId;

        @Value("${razorpay.keySecret}")
        private String keySecret;

        public PaymentController(PaymentService paymentService) {
            this.paymentService = paymentService;
        }

        @PostMapping("/orders")
        public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> req) throws RazorpayException {
            int amount = (int) req.getOrDefault("amount", 0);
            String currency = (String) req.getOrDefault("currency", "INR");
            String receipt = (String) req.getOrDefault("receipt", "rcpt_" + System.currentTimeMillis());

            // If client requested a payment link instead of an order
            boolean wantLink = false;
            Object linkObj = req.get("link");
            if (linkObj instanceof Boolean) {
                wantLink = (Boolean) linkObj;
            } else if (linkObj != null) {
                wantLink = Boolean.parseBoolean(String.valueOf(linkObj));
            }

            if (wantLink) {
                String description = (String) req.getOrDefault("description", "Payment");
                try {
                    String url = paymentService.createPaymentLink(amount, description);
                    return ResponseEntity.ok(Map.of("url", url));
                } catch (Exception e) {
                    return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
                }
            }

            Order order = paymentService.createOrder(amount, currency, receipt);

            Map<String, Object> payload = new HashMap<>();
            payload.put("orderId", order.get("id"));
            payload.put("amount", order.get("amount"));
            payload.put("currency", order.get("currency"));
            payload.put("key", keyId);

            // return the Map so Spring serializes it as JSON (not payload.toString())
            return ResponseEntity.ok(payload);
        }

        @PostMapping("/verify")
        public ResponseEntity<?> verifyPayment(@RequestBody Map<String, String> body) {
            String orderId = body.get("razorpay_order_id");
            String paymentId = body.get("razorpay_payment_id");
            String signature = body.get("razorpay_signature");

            try {
                JSONObject attributes = new JSONObject();
                attributes.put("razorpay_order_id", orderId);
                attributes.put("razorpay_payment_id", paymentId);
                attributes.put("razorpay_signature", signature);

                boolean verified = com.razorpay.Utils.verifyPaymentSignature(attributes, keySecret);
                return ResponseEntity.ok(Map.of("verified", verified));

            } catch (Exception e) {
                return ResponseEntity.badRequest().body(Map.of("verified", false, "error", e.getMessage()));
            }
        }

        @PostMapping("/payment-link")
        public ResponseEntity<?> createPaymentLink(@RequestBody Map<String, Object> req) {
            int amount = (int) req.getOrDefault("amount", 0);
            String description = (String) req.getOrDefault("description", "Payment");
            try {
                String url = paymentService.createPaymentLink(amount, description);
                return ResponseEntity.ok(Map.of("url", url));
            } catch (Exception e) {
                return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
            }
        }
    }