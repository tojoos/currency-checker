package tojoos.currencychecker.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tojoos.currencychecker.dto.CurrencyRequestDto;
import tojoos.currencychecker.exception.CurrencyNotFoundException;
import tojoos.currencychecker.exception.GlobalExceptionHandler;
import tojoos.currencychecker.exception.InvalidCurrencyCodeException;
import tojoos.currencychecker.mapper.CurrencyRequestMapper;
import tojoos.currencychecker.pojo.CurrencyRequest;
import tojoos.currencychecker.service.CurrencyRequestServiceImpl;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CurrencyRequestControllerTest {
  @Mock
  CurrencyRequestServiceImpl currencyRequestService;

  @InjectMocks
  CurrencyRequestController currencyRequestController;

  MockMvc mockMvc;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
    currencyRequestController = new CurrencyRequestController(currencyRequestService, new CurrencyRequestMapper());
    mockMvc = MockMvcBuilders
            .standaloneSetup(currencyRequestController)
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
  }

  @Test
  void testFindAllCurrencyRequests() throws Exception {
    //when
    CurrencyRequest currencyRequest1 = CurrencyRequest.builder().requesterName("Jan Kowalski").build();
    List<CurrencyRequest> currencyRequests = List.of(currencyRequest1, new CurrencyRequest(), new CurrencyRequest());
    when(currencyRequestService.findAll()).thenReturn(currencyRequests);

    //then
    MvcResult result = mockMvc.perform(get("/currencies/requests"))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$")
                    .isArray())
            .andExpect(jsonPath("$.[0].name")
                    .value("Jan Kowalski"))
            .andExpect(jsonPath("$.[1].name")
                    .isEmpty())
            .andReturn();

    String contentAsString = result.getResponse().getContentAsString();

    // Deserialize the JSON string into a List<CurrencyRequest>
    ObjectMapper objectMapper = new ObjectMapper();
    List<CurrencyRequestDto> currencyRequestList = objectMapper.readValue(contentAsString, new TypeReference<>() {});

    assertEquals(currencyRequests.size(), currencyRequestList.size());

    verify(currencyRequestService, times(1)).findAll();
  }

  @Test
  void testGetCurrentCurrencyValue() throws Exception {
    Double currencyValue = 5.0d;
    CurrencyRequest currencyRequest = CurrencyRequest.builder().currencyValue(currencyValue).build();

    //when
    when(currencyRequestService.add(any(CurrencyRequest.class))).thenReturn(currencyRequest);

    //then
    mockMvc.perform(post("/currencies/get-current-currency-value-command")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(currencyRequest)))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.value")
                    .value(currencyValue));

    verify(currencyRequestService, times(1)).add(any(CurrencyRequest.class));
  }

  @Test
  void testGetCurrentCurrencyValueInvalidCurrencyCode() throws Exception {
    CurrencyRequest currencyRequest = CurrencyRequest.builder().build();

    //when
    when(currencyRequestService.add(any(CurrencyRequest.class)))
            .thenThrow(new InvalidCurrencyCodeException("Requested currency code '1234' is invalid, it must be a 3-letter uppercase string"));

    //then
    mockMvc.perform(post("/currencies/get-current-currency-value-command")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(currencyRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.timestamp",
                    Matchers.notNullValue()))
        .andExpect(jsonPath("$.errorMessage",
                equalTo("Requested currency code '1234' is invalid, it must be a 3-letter uppercase string")));

    verify(currencyRequestService, times(1)).add(any(CurrencyRequest.class));
  }

  @Test
  void testGetCurrentCurrencyValueCurrencyCodeNotFound() throws Exception {
    CurrencyRequest currencyRequest = CurrencyRequest.builder().build();

    //when
    when(currencyRequestService.add(any(CurrencyRequest.class)))
            .thenThrow(new CurrencyNotFoundException("Provided currency code: 'XYZ' couldn't be found in NBP Web API."));

    //then
    mockMvc.perform(post("/currencies/get-current-currency-value-command")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(asJsonString(currencyRequest)))
            .andExpect(status().isNotFound())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.timestamp",
                    Matchers.notNullValue()))
            .andExpect(jsonPath("$.errorMessage",
                    equalTo("Provided currency code: 'XYZ' couldn't be found in NBP Web API.")));

    verify(currencyRequestService, times(1)).add(any(CurrencyRequest.class));
  }

  @Test
  void testGetCurrentCurrencyValueMissingRequestBody() throws Exception {
    //then
    mockMvc.perform(post("/currencies/get-current-currency-value-command"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.timestamp",
                    Matchers.notNullValue()))
            .andExpect(jsonPath("$.errorMessage",
                    Matchers.containsString("Required request body is missing.")));

    verify(currencyRequestService, times(0)).add(any(CurrencyRequest.class));
  }

  private static String asJsonString(final Object obj) {
    try {
      return new ObjectMapper().writeValueAsString(obj);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}