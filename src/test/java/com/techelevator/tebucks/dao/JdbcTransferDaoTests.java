package com.techelevator.tebucks.dao;

import com.techelevator.tebucks.security.dao.JdbcAccountDao;
import com.techelevator.tebucks.security.dao.JdbcTransferDao;
import com.techelevator.tebucks.security.dao.JdbcUserDao;
import com.techelevator.tebucks.security.model.Account;
import com.techelevator.tebucks.security.model.RegisterUserDto;
import com.techelevator.tebucks.security.model.Transfer;
import com.techelevator.tebucks.security.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.Assert.*;

public class JdbcTransferDaoTests extends BaseDaoTests{
    private JdbcAccountDao accountDao;
    private JdbcUserDao userDao;
    private JdbcTransferDao transferDao;
    @Before
    public void setup() {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        accountDao = new JdbcAccountDao(jdbcTemplate);
        userDao = new JdbcUserDao(jdbcTemplate, accountDao);
        transferDao = new JdbcTransferDao(jdbcTemplate);
    }

    @Test
    public void createTransfer_creates_valid_transfer() {
        int expectedFromId = 1;
        int expectedToId = 2;
        BigDecimal expectedAmount = new BigDecimal("250.00");

        Transfer transfer = new Transfer();
        transfer.setTransferType("Send");
        transfer.setTransferStatus("Approved");
        transfer.setUserFrom(userDao.getUserById(1));
        transfer.setUserTo(userDao.getUserById(2));
        transfer.setAmount(new BigDecimal("250.00"));

        Transfer result = transferDao.createTransfer(transfer);

        assertNotNull(result);
        assertTrue(result.getTransferId() > 0);
        assertEquals("Send", result.getTransferType());
        assertEquals("Approved", result.getTransferStatus());
        assertEquals(1, result.getUserFrom().getId());
        assertEquals(2, result.getUserTo().getId());
        assertEquals(expectedAmount, result.getAmount());
    }

    @Test
    public void getTransfersByUserId_given_valid_ID_returns_list(){
        int userId = 1;
        int expectedListSize = 3;
        List<Transfer> transferList = transferDao.getTransfersByUserId(userId);
        assertEquals(expectedListSize, transferList.size());
    }

    @Test
    public void getTransfersByUserId_given_invalid_ID_returns_empty_list(){
        int userId = -1;
        int expectedListSize = 0;
        List<Transfer> transferList = transferDao.getTransfersByUserId(userId);
        assertEquals(expectedListSize, transferList.size());
    }

    @Test
    public void getTransferById_given_valid_id_returns_transfer(){
        int expectedFromId = 1;
        int expectedToId = 2;
        BigDecimal expectedAmount = new BigDecimal("200.00");

        Transfer testTransfer = transferDao.getTransferById(1);
        assertNotNull(testTransfer);
        assertTrue(testTransfer.getTransferId() > 0);
        assertEquals("Request", testTransfer.getTransferType());
        assertEquals("Rejected", testTransfer.getTransferStatus());
        assertEquals(1, testTransfer.getUserFrom().getId());
        assertEquals(2, testTransfer.getUserTo().getId());
        assertEquals(expectedAmount, testTransfer.getAmount());
    }
    @Test
    public void getTransferById_given_invalid_id_returns_null(){
        Transfer testTransfer = transferDao.getTransferById(-1);
        assertNull(testTransfer);
    }


    @Test
    public void approveTransfer_given_valid_transferId_and_correct_userTo_returns_approved_transfer(){
        int testTransferId = 3;
        int testUserId = 3;
        transferDao.approveTransfer(testTransferId, testUserId);

        String expectedTransferStatus = "Approved";

        assertEquals(expectedTransferStatus, transferDao.getTransferById(3).getTransferStatus());
    }
    @Test
    public void rejectTransfer_given_valid_transferId_and_valid_userTo_returns_updated_transfer(){
        int testTransferId = 3;
        int testUserId = 3;
        transferDao.rejectTransfer(testTransferId, testUserId);

        String expectedTransferStatus = "Rejected";

        assertEquals(expectedTransferStatus, transferDao.getTransferById(3).getTransferStatus());

    }
}
