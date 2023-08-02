package tojoos.currencychecker.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tojoos.currencychecker.dto.CurrencyRequestDto;
import tojoos.currencychecker.dto.CurrencyValueDto;
import tojoos.currencychecker.mapper.CurrencyRequestMapper;
import tojoos.currencychecker.pojo.CurrencyRequest;
import tojoos.currencychecker.service.CurrencyRequestService;

import java.util.List;

@RestController
@RequestMapping("/currencies")
@CrossOrigin("*")
public class CurrencyRequestController {
    private final CurrencyRequestService currencyRequestService;
    private final CurrencyRequestMapper currencyRequestMapper;

    public CurrencyRequestController(CurrencyRequestService currencyRequestService, CurrencyRequestMapper currencyRequestMapper) {
        this.currencyRequestService = currencyRequestService;
        this.currencyRequestMapper = currencyRequestMapper;
    }

    @GetMapping("/requests")
    public ResponseEntity<List<CurrencyRequestDto>> findAll() {
        List<CurrencyRequest> foundCurrencyRequests = currencyRequestService.findAll();
        List<CurrencyRequestDto> foundCurrencyRequestsDTOs = foundCurrencyRequests.stream()
                .map(currencyRequestMapper::toDto)
                .toList();
        return new ResponseEntity<>(foundCurrencyRequestsDTOs, HttpStatus.OK);
    }

    @PostMapping("/get-current-currency-value-command")
    public ResponseEntity<CurrencyValueDto> getCurrentCurrencyValue(@RequestBody CurrencyRequestDto currencyRequestDto) {
        // attempt adding new record to db - validate currency code - make a request - save record or throw exception
        CurrencyRequest currencyRequest = currencyRequestService.add(currencyRequestMapper.toPojo(currencyRequestDto));
        return new ResponseEntity<>(new CurrencyValueDto(currencyRequest.getCurrencyValue()), HttpStatus.OK);
    }
}
