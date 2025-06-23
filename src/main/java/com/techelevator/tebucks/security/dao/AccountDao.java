package com.techelevator.tebucks.security.dao;

import com.techelevator.tebucks.security.model.Account;

import java.math.BigDecimal;

public interface AccountDao {
    Account getAccountById(int accountId);
    Account getAccountByUserId(int userId);
    BigDecimal getBalanceByUserId(int userId);
    Account updateBalance(int accountId, BigDecimal newBalance);
    Account createAccount(int userId);
}
