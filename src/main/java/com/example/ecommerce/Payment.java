package com.example.ecommerce;

import java.time.LocalDate;

public abstract class Payment implements Pay {
    protected int paymentId;
    protected LocalDate paymentDate;
    protected double amount;
    protected String method;
    protected Customer customer;
    public Payment(int paymentId, LocalDate paymentDate, double amount, String method,Customer customer) {
        this.paymentId = paymentId;
        this.paymentDate = paymentDate;
        this.amount = amount;
        this.method = method;
        this.customer=customer;
    }
    public abstract void makePayment(double amount);
    public abstract void printReceipt();

}
