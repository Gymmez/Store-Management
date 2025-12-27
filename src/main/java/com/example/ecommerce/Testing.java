package com.example.ecommerce;

public class Testing {
    public static void main(String[] args) throws Exception {

        String jsonBody = """
                {
                  "paymentorderID": 5,
                  "amount": 30,
                  "holder": "John Doe",
                  "cardnumber": "1234"
                }
                """;
        String res=EcommerceApi.getCustomer(1001);
        System.out.println(res);
    }
}
