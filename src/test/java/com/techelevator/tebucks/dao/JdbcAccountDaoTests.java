package com.techelevator.tebucks.dao;

import com.fasterxml.jackson.databind.ser.Serializers;
import com.techelevator.tebucks.security.dao.JdbcAccountDao;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.model.Account;
import com.techelevator.tebucks.security.model.RegisterUserDto;
import com.techelevator.tebucks.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class JdbcAccountDaoTests extends BaseDaoTests {
    protected static final Account USER_1 = new Account();
    protected static final Account ACCOUNT_1 = new Account(1,1, BigDecimal.valueOf(1000.00));
    private static final Account USER_3 = new Account(2,2, BigDecimal.valueOf(1000.00));

    private JdbcAccountDao accountDao;
    private JdbcUserDao userDao;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        userDao = new JdbcUserDao(jdbcTemplate, accountDao);
    }
    @Test
    public void getAccountByAccountID_given_invalidID_returns_null() {
        Account actualAccount = accountDao.getAccountById(-1);
        assertNull(actualAccount);
    }
    @Test
    public void createAccount_creates_a_account() {
        RegisterUserDto user = new RegisterUserDto();
        user.setUsername("new");
        user.setPassword("USER");

        User createdUser = userDao.createUser(user);

        Assert.assertNotNull(createdUser);

        Account newAccount = accountDao.createAccount(createdUser.getId());
        assertNotNull(newAccount);

        Account account = accountDao.getAccountByUserId(createdUser.getId());
        Assert.assertNotNull("Account should be created for the user", account);
        Assert.assertEquals(new BigDecimal("1000.00"), account.getBalance());
        Assert.assertEquals(createdUser.getId(), account.getUserId());
    }

    @Test
    public void getAccountById_given_valid_id_returns_account(){
        int accountId = 3;
        assertNotNull(accountDao.getAccountById(accountId));
    }
    @Test
    public void getAccountById_given_invalid_id_returns_account(){
        int accountId = -1;
        assertNull(accountDao.getAccountById(accountId));
    }
    @Test
    public void getAccountByUserId_given_valid_id_returns_account(){
        int userId = 3;
        assertNotNull(accountDao.getAccountByUserId(userId));
    }
    @Test
    public void getAccountUserById_given_invalid_id_returns_account(){
        int accountId = -1;
        assertNull(accountDao.getAccountByUserId(accountId));
    }

    @Test
    public void getBalanceByUserId_given_valid_id_returns_balance(){
        int userId = 3;
        BigDecimal expectedValue = new BigDecimal("1000.00");
        assertEquals(expectedValue, accountDao.getBalanceByUserId(userId));
    }
    @Test
    public void updateBalance_given_validId_returns_account_with_new_balance(){
        int accountId = 1;
        BigDecimal expectedBalance = new BigDecimal("1500.00");
        BigDecimal newBalance = new BigDecimal("1500.00");

        accountDao.updateBalance(accountId, newBalance);

        Account updatedAccount = accountDao.getAccountById(accountId);
        assertEquals(expectedBalance, updatedAccount.getBalance());
    }




}
