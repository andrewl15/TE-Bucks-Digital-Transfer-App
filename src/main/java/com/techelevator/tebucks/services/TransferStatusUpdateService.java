package com.techelevator.tebucks.services;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.security.dao.AccountDao;
import com.techelevator.tebucks.security.dao.TransferDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.Account;
import com.techelevator.tebucks.security.model.Transfer;
import com.techelevator.tebucks.security.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.model.User;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.security.Principal;

@Service
public class TransferStatusUpdateService {
    private final String SENDER_STATUS_APPROVED = "Approved";
    private final String SENDER_STATUS_REJECTED = "Rejected";
    private final AccountDao accountDao;
    private final TransferDao transferDao;
    private final UserDao userDao;

    public TransferStatusUpdateService(AccountDao accountDao, TransferDao transferDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.transferDao = transferDao;
        this.userDao = userDao;
    }

    public Transfer updateTransferStatus(TransferStatusUpdateDto dto, int id, Principal principle) {
        try {
            Transfer transfer = transferDao.getTransferById(id);
            if (transfer == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Transfer could not be found");
            }

            User authenticatedUser = userDao.getUserByUsername(principle.getName());
            if (transfer.getUserFrom().getId() != authenticatedUser.getId()) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "You do not have access to change this request status");
            }

            String updatedStatus = dto.getTransferStatus();
            if (updatedStatus.equalsIgnoreCase(SENDER_STATUS_APPROVED)) {
                Account sender = accountDao.getAccountByUserId(authenticatedUser.getId());

                if (sender.getBalance().compareTo(transfer.getAmount()) < 0) {
                    throw new IllegalArgumentException("Transfer amount must be positive.");
                }

                accountDao.updateBalance(sender.getUserId(), sender.getBalance().subtract(transfer.getAmount()));
                Account receiver = accountDao.getAccountByUserId(transfer.getUserTo().getId());
                accountDao.updateBalance(receiver.getUserId(), receiver.getBalance().add(transfer.getAmount()));

                if(transfer.getAmount().compareTo(new BigDecimal("1000")) > -1 ){
                    LogService.over1kLogTransfer(transfer.getUserFrom().getUsername(), sender.getUserId(), transfer.getAmount());
                }
                return transferDao.approveTransfer(id, receiver.getUserId());
            } else if (updatedStatus.equalsIgnoreCase(SENDER_STATUS_REJECTED)) {
                Account receiver = accountDao.getAccountByUserId(transfer.getUserTo().getId());
                return transferDao.rejectTransfer(id, receiver.getUserId());
            } else {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Request must be approved or rejected!");
            }
        } catch (DaoException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
