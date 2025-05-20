package com.spring.springRestDemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.spring.springRestDemo.model.Account;
import com.spring.springRestDemo.service.AccountService;

@Component
public class SeedData implements CommandLineRunner{

    @Autowired
    private AccountService accountService;
    @Override
    public void run(String... args) throws Exception{
        Account account01=new Account();
        Account account02=new Account();

        account01.setEmail("user@user.com");
        account01.setPassword("password");
        account01.setRole("ROLE_USER");
        accountService.save(account01);

        account02.setEmail("admin@admin.com");
        account02.setPassword("password");
        account02.setRole("ROLE_ADMIN");
        accountService.save(account02);

    }
    
}
