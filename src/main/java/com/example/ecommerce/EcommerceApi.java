package com.example.ecommerce;

import java.io.IOException;

public final class EcommerceApi {

    private static final String BASE_URL = "http://127.0.0.1:8080";
    private static final ApiClient api = ApiClient.getInstance(BASE_URL);


    private EcommerceApi() {}


    // GET /order/<id>/getorder
    public static String getOrder(int orderId)
            throws IOException, InterruptedException {
        return api.get("/order/" + orderId + "/getorder");
    }

    // POST /order/postorder
    public static String postOrder(String orderJson)
            throws IOException, InterruptedException {
        return api.post("/order/postorder", orderJson);
    }


    // GET /customer/<id>/getcustomer
    public static String getCustomer(int customerId)
            throws IOException, InterruptedException {
        return api.get("/customer/" + customerId + "/getcustomer");
    }


    // POST /payment/postpayment
    public static String postPayment(String paymentJson)
            throws IOException, InterruptedException {
        return api.post("/payment/postpayment", paymentJson);
    }
}
