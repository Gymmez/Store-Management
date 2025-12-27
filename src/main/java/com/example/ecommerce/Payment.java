package com.example.ecommerce;

import java.time.LocalDate;

public abstract class Payment implements Pay {
    protected int paymentId;
    protected double amount;
    protected String method;
    public Payment(int paymentId, double amount, String method) {
        this.paymentId = paymentId;
        this.amount = amount;
        this.method = method;

    }
    public abstract void makePayment(double amount);
    public abstract void printReceipt();

}
