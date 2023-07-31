package tojoos.currencychecker.service;

import tojoos.currencychecker.pojo.CurrencyRequest;

import java.util.List;

public interface CurrencyRequestService {
    List<CurrencyRequest> findAll();
    CurrencyRequest add(CurrencyRequest currencyRequest);
}
