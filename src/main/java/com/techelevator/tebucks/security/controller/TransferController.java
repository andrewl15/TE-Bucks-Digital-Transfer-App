package com.techelevator.tebucks.security.controller;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.security.dao.TransferDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.model.NewTransferDto;
import com.techelevator.tebucks.security.model.Transfer;
import com.techelevator.tebucks.security.model.TransferStatusUpdateDto;
import com.techelevator.tebucks.security.model.User;
import com.techelevator.tebucks.services.TransferService;
import com.techelevator.tebucks.services.TransferStatusUpdateService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.security.Principal;


import java.util.List;

@RestController
@RequestMapping("/api")
public class TransferController {
    private final TransferDao transferDao;
    private final UserDao userDao;
    private final TransferService transferService;
    private final TransferStatusUpdateService transferStatusUpdateService;

    public TransferController(TransferDao transferDao, UserDao userDao, TransferService transferService, TransferStatusUpdateService updateTransferStatusService){
        this.userDao = userDao;
        this.transferDao = transferDao;
        this.transferService = transferService;
        this.transferStatusUpdateService = updateTransferStatusService;
    }

    @GetMapping(path = "/transfers/{id}")
    public Transfer getTransferById(@PathVariable int id) {
        Transfer output = null;
        try {
            output = transferDao.getTransferById(id);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        if (output == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return output;
    }
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/account/transfers")
    public List<Transfer> getTransfers(Principal principal) {
        try {
            User user = userDao.getUserByUsername(principal.getName());
            return transferDao.getTransfersByUserId(user.getId());
        } catch(DaoException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(path = "/transfers")
    public Transfer newTransfer(@RequestBody NewTransferDto newTransferDto, Principal principal){
        try{
            return transferService.sendTransfer(newTransferDto, principal);
        } catch(DaoException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping(path = "/transfers/{id}/status")
    public Transfer updateTransferStatus(@RequestBody TransferStatusUpdateDto transferStatusUpdateDto, @PathVariable int id, Principal principal){
        try{
            return transferStatusUpdateService.updateTransferStatus(transferStatusUpdateDto, id, principal);
        } catch(DaoException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
