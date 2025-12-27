package com.example.ecommerce;

public class Customer {
    private int customerID;
    private String name;
    private String address;

    public Customer(int customerID, String name, String address) {
        this.customerID = customerID;
        this.name = name;
        this.address = address;
    }

    // Getters
    public int getCustomerID() {
        return customerID;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    // Setters
    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    // Convert Customer to JSON string manually
    public String toJson() {
        return """
            {
                "id": %d,
                "name": "%s",
                "address": "%s"
            }
            """.formatted(customerID, name, address);
    }

    @Override
    public String toString() {
        return "Customer{id=" + customerID + ", name='" + name + "', address='" + address + "'}";
    }
}
