package com.spring.springRestDemo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.spring.springRestDemo.model.Account;
import java.util.Optional;


public interface AccountRepository extends JpaRepository<Account,Long>{
    Optional<Account> findByEmail(String email);
    
    
}
