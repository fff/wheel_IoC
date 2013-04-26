package com.thoughtworks.wheels.beans;

public class Customer {
    private String customerId;
    private CustomerName customerName;
    private Customer friend;

    public Customer(Customer friend) {
        this.friend = friend;
    }

    public Customer() {
    }

    public Customer(String customerId, CustomerName customerName) {
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public CustomerName getCustomerName() {
        return customerName;
    }

    public void setCustomerName(CustomerName customerName) {
        this.customerName = customerName;
    }
}
