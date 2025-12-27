package com.example.ecommerce;

import com.example.ecommerce.Customer;
import com.example.ecommerce.Payment;

import java.time.LocalDate;

public class VisaPay extends Payment {
    String holderName;
    String cardNumber;
    public VisaPay(int paymentId, double amount, String holderName, String cardNumber){
        super(paymentId, amount, "Visa");
        this.holderName=holderName;
        this.cardNumber=cardNumber;
    }
    public void makePayment(double amount) {
        System.out.println("Paid $" + amount + " in Visa.");
    }
    public String getHolderName(){
        return this.holderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    @Override
    public void printReceipt() {

    }
    public String toPostJson(int paymentorderID){
        return """
            {
                "paymentorderID": %d,
                "amount": %.2f,
                "holder": "%s",
                "cardnumber": %s
            }
            """.formatted(paymentorderID,amount,getHolderName(),getCardNumber() );
    }
}
