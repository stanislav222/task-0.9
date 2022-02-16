package com.example.demo.external.alfabank.model;

public enum  Currency {
    RUB(643),
    USD(840),
    EUR(978);

    private final int currencyCode;

    Currency(int currencyCode) {
        this.currencyCode = currencyCode;
    }

    public int getCurrencyCode() {
        return currencyCode;
    }

}
