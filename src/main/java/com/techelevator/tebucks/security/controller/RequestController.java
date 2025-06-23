package com.techelevator.tebucks.security.controller;

import com.techelevator.tebucks.security.dao.UserDao;
import com.techelevator.tebucks.security.jwt.TokenProvider;
import com.techelevator.tebucks.security.model.Account;
import com.techelevator.tebucks.security.model.LoginDto;
import com.techelevator.tebucks.security.model.LoginResponseDto;
import com.techelevator.tebucks.security.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.web.bind.annotation.*;

@PreAuthorize("isAuthenticated()")
@RestController
@RequestMapping("/api/")
public class RequestController {

    private final UserDao userDao;

    private final TokenProvider tokenProvider;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    public RequestController(UserDao userDao, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
        this.userDao = userDao;
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
    }


//    @GetMapping(path = "account/balance")
//    public Account getAccountBalance(){
//        Account account = new Account();
//        try{
//            return ;
//        }
//    }
}
