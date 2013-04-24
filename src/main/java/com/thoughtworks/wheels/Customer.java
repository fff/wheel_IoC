package com.thoughtworks.wheels;

public class Customer {
    private String customerId;
    private Name customerName;

    public Customer(String customerId, Name customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
