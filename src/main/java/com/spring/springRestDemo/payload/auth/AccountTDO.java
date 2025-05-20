package com.spring.springRestDemo.payload.auth;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AccountTDO {
    private String email;
    private String password;

}
