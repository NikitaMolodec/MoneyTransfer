package com.molodec.nikita.transfer.model;

public enum Currency {
    EUR("EUR"), USD("USD"), RUB("RUB");

    private final String currencyCode;

    Currency(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String currencyCode() {
        return currencyCode;
    }
}
