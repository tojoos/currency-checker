package tojoos.currencychecker.service;

import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import tojoos.currencychecker.exception.CurrencyNotFoundException;
import tojoos.currencychecker.exception.InvalidCurrencyCodeException;
import tojoos.currencychecker.exception.UnhandledResponseException;
import tojoos.currencychecker.pojo.CurrencyRequest;
import tojoos.currencychecker.repository.CurrencyRequestRepository;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CurrencyRequestServiceImpl implements CurrencyRequestService {
    private final String nbpApiCurrencyUrl = "http://api.nbp.pl/api/exchangerates/rates/a";
    private final String currencyCodeRegex = "[a-zA-Z]{3}";

    private final CurrencyRequestRepository currencyRequestRepository;

    public CurrencyRequestServiceImpl(CurrencyRequestRepository currencyRequestRepository) {
        this.currencyRequestRepository = currencyRequestRepository;
    }

    @Override
    public List<CurrencyRequest> findAll() {
        log.info("Getting all currencyRequests.");
        return currencyRequestRepository.findAll();
    }

    @Override
    public CurrencyRequest add(CurrencyRequest currencyRequest) {
        currencyRequest.setDate(LocalDateTime.now());
        currencyRequest.setCurrencyValue(this.getCurrencyValue(currencyRequest.getCurrencyCode()));

        CurrencyRequest savedCurrencyRequest = currencyRequestRepository.save(currencyRequest);
        log.info("Added new #" + savedCurrencyRequest.getId() + " currency request: " + savedCurrencyRequest);
        return savedCurrencyRequest;
    }

    public Double getCurrencyValue(String currencyCode) {
        // validation of the currency code field
        if (currencyCode == null || !currencyCode.matches(currencyCodeRegex)) {
            throw new InvalidCurrencyCodeException("Requested currency code '" + currencyCode + "' is invalid, it must be a 3-letter uppercase string");
        }

        String fullNbpApiUrl = this.nbpApiCurrencyUrl + "/" + currencyCode + "?format=json";

        try {
            return this.performExternalApiRequest(fullNbpApiUrl, currencyCode);
        } catch (IOException | InterruptedException e) {
            log.error("Error occurred while performing the request to url: {}, error message: {}",  fullNbpApiUrl, e.getMessage());
            throw new RuntimeException("Error occurred while performing the request to url: " + fullNbpApiUrl + ", error message: " + e.getMessage());
        }
    }

    private Double performExternalApiRequest(String url, String currencyCode) throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder(
                URI.create(url))
            .header("accept", "application/json")
            .timeout(Duration.ofSeconds(30))
            .build();

        log.info("Performing request [GET] on url: {}", url);

        HttpResponse<String> httpResponse = HttpClient
            .newHttpClient()
            .send(httpRequest, HttpResponse.BodyHandlers.ofString());

        int statusCode = httpResponse.statusCode();

        switch (statusCode) {
            case 200 -> {
                String response = httpResponse.body();
                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getJSONArray("rates").getJSONObject(0).getDouble("mid");
            }
            case 404 ->
                throw new CurrencyNotFoundException("Provided currency code: '" + currencyCode + "' couldn't be found in NBP Web API.");
            case 400 ->
                // according to nbp api docs 400 error code is caused by incorrectly formulated query: https://api.nbp.pl
                throw new UnhandledResponseException("Incorrectly formulated query to nbp api: " + url);
            default ->
                // handle other status codes if necessary
                throw new UnhandledResponseException("Unhandled status code: " + statusCode);
        }
    }
}
