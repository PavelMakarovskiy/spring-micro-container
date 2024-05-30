package ru.jb.micro.planner.entity.ps;

import lombok.Data;

import javax.money.CurrencyUnit;

@Data
public class Account {
    private String id;
    private String currency;
    private String country;
    private long reserve;
}
