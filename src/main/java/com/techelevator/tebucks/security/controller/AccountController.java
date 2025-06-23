package com.techelevator.tebucks.security.controller;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.security.dao.AccountDao;
import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.jwt.TokenProvider;
import com.techelevator.tebucks.security.model.Account;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;

@RestController
@RequestMapping("/api/account")
public class AccountController {
    private final AccountDao accountDao;

    private final UserDao userDao;

    public AccountController(AccountDao accountDao, UserDao userDao) {
        this.accountDao = accountDao;
        this.userDao = userDao;
    }


    @GetMapping(path = "/balance")
    public Account getBalance(Principal principal){
        try{
            String username = principal.getName();
            int userId = userDao.getUserByUsername(username).getId();
            return accountDao.getAccountByUserId(userId);
        } catch(DaoException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

    }
}
