package com.spring.springRestDemo.util.constants;

public enum Authority {
    READ,
    WRITE,
    UPDATE,
    USER, //USER CAN UPDATE DELETE SELF OBJECT, AND READ ANYTHING
    ADMIN  //ADMIN CAN READ UPDATE DELETE ANY OBJECT
}
