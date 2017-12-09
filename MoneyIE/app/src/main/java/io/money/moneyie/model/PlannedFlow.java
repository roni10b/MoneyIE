package io.money.moneyie.model;

public class PlannedFlow {
    private String userID;
    private int date;
    private String type;
    private double amount;

    public PlannedFlow(String userID, int date, String type, double amount) {
        this.userID = userID;
        this.date = date;
        this.type = type;
        this.amount = amount;
    }

    public String getUserID() {
        return userID;
    }

    public int getDate() {
        return date;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }
}
