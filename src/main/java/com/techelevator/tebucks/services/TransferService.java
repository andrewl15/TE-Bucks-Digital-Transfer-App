package com.techelevator.tebucks.services;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.security.dao.AccountDao;
import com.techelevator.tebucks.security.dao.TransferDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;

@Service
public class TransferService {
    private final String SENDER_STATUS_PENDING = "Pending";
    private final String SENDER_STATUS_APPROVED = "Approved";
    private final String TRANSFER_TYPE_SEND = "Send";
    private final String TRANSFER_TYPE_REQUEST = "Request";
    private final AccountDao accountDao;
    private final TransferDao transferDao;
    private final UserDao userDao;

    public TransferService(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    public Transfer sendTransfer(NewTransferDto dto, Principal principal) {
        // Validate
        if (dto.getUserFrom() == dto.getUserTo()) {
            throw new IllegalArgumentException("Cannot send money to yourself.");
        }

        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }



        if(dto.getTransferType().equalsIgnoreCase(TRANSFER_TYPE_SEND)){
            Account sender = accountDao.getAccountByUserId(dto.getUserFrom());

            if (sender.getBalance().compareTo(dto.getAmount()) < 0) {
                LogService.overDraftLog(sender.getUserId(), sender.getBalance());
                throw new IllegalArgumentException("Insufficient funds.");
            }
            Transfer transfer = new Transfer();
            transfer.setUserFrom(userDao.getUserById(dto.getUserFrom()));
            transfer.setUserTo(userDao.getUserById(dto.getUserTo()));
            transfer.setAmount(dto.getAmount());
            transfer.setTransferType(TRANSFER_TYPE_SEND);
            transfer.setTransferStatus(SENDER_STATUS_APPROVED);

            Transfer outTransfer = transferDao.createTransfer(transfer);

            accountDao.updateBalance(dto.getUserFrom(), sender.getBalance().subtract(dto.getAmount()));
            Account receiver = accountDao.getAccountByUserId(dto.getUserTo());
            accountDao.updateBalance(dto.getUserTo(), receiver.getBalance().add(dto.getAmount()));

            if(transfer.getAmount().compareTo(new BigDecimal("1000")) > -1 ){
                LogService.over1kLogTransfer(transfer.getUserFrom().getUsername(), sender.getUserId(), transfer.getAmount());
            }
            return outTransfer;
        }else if(dto.getTransferType().equalsIgnoreCase(TRANSFER_TYPE_REQUEST)){
            Transfer transfer = new Transfer();
            transfer.setUserFrom(userDao.getUserById(dto.getUserFrom()));
            transfer.setUserTo(userDao.getUserById(dto.getUserTo()));
            transfer.setAmount(dto.getAmount());
            transfer.setTransferType(TRANSFER_TYPE_REQUEST);
            transfer.setTransferStatus(SENDER_STATUS_PENDING);

            return transferDao.createTransfer(transfer);
        } else{
            throw new IllegalArgumentException("Invalid transfer type.");
        }


    }


}
