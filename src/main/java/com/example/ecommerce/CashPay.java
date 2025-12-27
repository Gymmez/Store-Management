package com.example.ecommerce;

import com.example.ecommerce.Payment;

import java.time.LocalDate;

public class CashPay extends Payment {
    public CashPay(int paymentId, LocalDate paymentDate, double amount,Customer customer){
        super(paymentId,paymentDate, amount, "Cash",customer);
    }
    public void makePayment(double amount) {
        System.out.println("Paid $" + amount + " in CASH.");
    }

    @Override
    public void printReceipt() {
        customer.getOrderinfo();
    }
}
