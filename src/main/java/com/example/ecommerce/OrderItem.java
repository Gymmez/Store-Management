package com.example.ecommerce;

public class OrderItem {
    private String orderItemID;
    private String itemName;
    private double itemPrice;

    public OrderItem(String orderItemID, String itemName, double itemPrice) {
        this.orderItemID = orderItemID;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    // Getters
    public String getOrderItemID() {
        return orderItemID;
    }

    public String getItemName() {
        return itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    // Setters
    public void setOrderItemID(String orderItemID) {
        this.orderItemID = orderItemID;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }

    // Convert to JSON string
    public String toJson() {
        return """
            {
                "name": "%s",
                "price": %.2f
            }
            """.formatted(getItemName(), getItemPrice());
    }

    @Override
    public String toString() {
        return itemName + "  $" + itemPrice;
    }
}
