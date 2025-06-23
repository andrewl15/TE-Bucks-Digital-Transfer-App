package com.techelevator.tebucks.security.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.security.model.Account;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.xml.crypto.Data;
import java.math.BigDecimal;

@Component
public class JdbcAccountDao implements AccountDao{
    private final JdbcTemplate jdbcTemplate;

    public JdbcAccountDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Account getAccountById(int accountId) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE account_id = ?;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, accountId);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataAccessException e){
            return null;
        }
        return account;
    }

    @Override
    public Account getAccountByUserId(int userId) {
        Account account = null;
        String sql = "SELECT * FROM account WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                account = mapRowToAccount(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return account;

    }

    @Override
    public BigDecimal getBalanceByUserId(int userId) {
        String sql = "SELECT balance FROM account WHERE user_id = ?";
        try {
            return jdbcTemplate.queryForObject(sql, BigDecimal.class, userId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }

    }

    @Override
    public Account updateBalance(int accountId, BigDecimal newBalance) {
        Account updatedAccountBalance = null;
        String sql = "UPDATE account SET balance = ? WHERE account_id = ?";
        try{
            int numberOfRows = jdbcTemplate.update(sql,newBalance, accountId);

            if (numberOfRows == 0){
                throw new DaoException("Zero rows affected, expected at least one");

            }else {
                updatedAccountBalance = getAccountById(accountId);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
        return updatedAccountBalance;
    }

    @Override
    public Account createAccount(int userId) {
        final BigDecimal defaultBalance = new BigDecimal("1000.00");
        String sql = "INSERT INTO account (user_id, balance) VALUES (?,?) RETURNING account_id";
        try{
            int newAccountId = jdbcTemplate.queryForObject(sql, int.class, userId, defaultBalance.doubleValue());
            return getAccountById(newAccountId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private Account mapRowToAccount(SqlRowSet rs){
        Account account = new Account();
        account.setAccountId(rs.getInt("account_id"));
        account.setUserId(rs.getInt("user_id"));
        account.setBalance(rs.getBigDecimal("balance"));
        return account;
    }
}
