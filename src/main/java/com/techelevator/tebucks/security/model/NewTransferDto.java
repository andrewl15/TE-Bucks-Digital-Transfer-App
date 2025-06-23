package com.techelevator.tebucks.security.model;

import java.math.BigDecimal;

public class NewTransferDto {
    private int userFrom;
    private int userTo;
    private BigDecimal amount;
    private String transferType;

    public int getUserTo() {
        return userTo;
    }

    public void setUserTo(int userTo) {
        this.userTo = userTo;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransferType() {
        return transferType;
    }

    public void setTransferType(String transferType) {
        this.transferType = transferType;
    }

    public int getUserFrom() {
        return userFrom;
    }
    public void setUserFrom(int userFrom){
        this.userFrom = userFrom;
    }
}

