package tojoos.currencychecker.service;

import com.fasterxml.jackson.databind.util.JSONPObject;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import tojoos.currencychecker.exception.InvalidCurrencyCodeException;
import tojoos.currencychecker.pojo.CurrencyRequest;
import tojoos.currencychecker.repository.CurrencyRequestRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
        currencyRequest.setCurrencyValue(getCurrencyValueFromExternalApi(currencyRequest.getCurrencyCode()));

        CurrencyRequest savedTask = currencyRequestRepository.save(currencyRequest);
        log.info("Added new currency request with id = \"" + savedTask.getId() + "\".");
        return savedTask;
    }

    public Double getCurrencyValueFromExternalApi(String currencyCode) {
        //validation of the currency code field
        if (currencyCode == null || !currencyCode.matches(currencyCodeRegex)) {
            throw new InvalidCurrencyCodeException("Currency code must be a 3-letter uppercase string");
        }

        String fullNbpApiUrl = this.nbpApiCurrencyUrl + "/" + currencyCode + "?format=json";

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder(
                    URI.create(fullNbpApiUrl))
                    .header("accept", "application/json")
                    .timeout(Duration.ofSeconds(30))
                    .build();

            HttpResponse<String> httpResponse = HttpClient.newHttpClient().send(httpRequest, HttpResponse.BodyHandlers.ofString());

            int statusCode = httpResponse.statusCode();

            if (statusCode == 200) {
                String response = httpResponse.body();

                JSONObject jsonObject = new JSONObject(response);
                return jsonObject.getJSONArray("rates").getJSONObject(0).getDouble("mid");
            } else {
                // obsliuzyc 404 - nie ma takiej waluty
                // obsluzyc 400 - bad request: https://api.nbp.pl
                System.out.println("GET request failed: HTTP error code " + statusCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        //request and return value
        return 0.0d;
    }
}
