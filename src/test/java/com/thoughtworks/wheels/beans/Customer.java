package com.thoughtworks.wheels.beans;

public class Customer {
    private String customerId;
    private CustomerName customerName;

    public Customer(String customerId, CustomerName customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }
}
