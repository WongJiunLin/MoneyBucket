package com.example.moneybucket.models;

public class Basket {
    private String basketName, basketBalance, basketBudget;

    public Basket() {
    }

    public Basket(String basketName, String basketBalance, String basketBudget) {
        this.basketName = basketName;
        this.basketBalance = basketBalance;
        this.basketBudget = basketBudget;
    }

    public String getBasketName() {
        return basketName;
    }

    public void setBasketName(String basketName) {
        this.basketName = basketName;
    }

    public String getBasketBalance() {
        return basketBalance;
    }

    public void setBasketBalance(String basketBalance) {
        this.basketBalance = basketBalance;
    }

    public String getBasketBudget() {
        return basketBudget;
    }

    public void setBasketBudget(String basketBudget) {
        this.basketBudget = basketBudget;
    }
}
