package tojoos.currencychecker.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import tojoos.currencychecker.exception.CurrencyNotFoundException;
import tojoos.currencychecker.exception.InvalidCurrencyCodeException;
import tojoos.currencychecker.exception.UnhandledResponseException;
import tojoos.currencychecker.pojo.CurrencyRequest;
import tojoos.currencychecker.repository.CurrencyRequestRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CurrencyRequestServiceImplTest {

  @Mock
  CurrencyRequestRepository currencyRequestRepository;

  @InjectMocks
  CurrencyRequestServiceImpl currencyRequestService;

  @BeforeEach
  public void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findAll() {
    List<CurrencyRequest> currencyRequests = List.of(new CurrencyRequest(), new CurrencyRequest(), new CurrencyRequest());

    //when
    when(currencyRequestRepository.findAll()).thenReturn(currencyRequests);

    //then
    List<CurrencyRequest> currencyRequestsFound = currencyRequestRepository.findAll();

    assertNotNull(currencyRequestsFound);
    assertEquals(currencyRequests.size(), currencyRequestsFound.size());
    verify(currencyRequestRepository, times(1)).findAll();
  }

  @Test
  void testAdd() {
    String currencyCode = "USD";
    double currencyValueReturned = 5.0d;
    CurrencyRequest currencyRequest = CurrencyRequest.builder().id(1L).currencyCode(currencyCode).build();
    // Creating a spy to mock only one method
    CurrencyRequestService currencyRequestServiceSpy = spy(currencyRequestService);

    //when
    when(currencyRequestRepository.save(any())).thenReturn(currencyRequest);
    // mock returned getCurrencyValue(String currencyCode) method
    doReturn(currencyValueReturned).when(currencyRequestServiceSpy).getCurrencyValue(anyString());

    //then
    CurrencyRequest returnedCurrencyRequest = currencyRequestServiceSpy.add(currencyRequest);

    assertNotNull(returnedCurrencyRequest);
    // check if date was set upon adding new currencyRequest
    assertNotNull(returnedCurrencyRequest.getDate());
    // check if value was set correctly
    assertEquals(currencyValueReturned, returnedCurrencyRequest.getCurrencyValue());
    verify(currencyRequestRepository, times(1)).save(any());
    verify(currencyRequestServiceSpy, times(1)).getCurrencyValue(currencyCode);
  }

  @Test
  void testAddInvalidCurrencyCode() {
    String invalidCurrencyCode = "TEST";
    CurrencyRequest currencyRequest = CurrencyRequest.builder().id(1L).currencyCode(invalidCurrencyCode).build();
    // Creating a spy to mock only one method
    CurrencyRequestService currencyRequestServiceSpy = spy(currencyRequestService);

    // mock exception throw on getCurrencyValue(String currencyCode) method
    doThrow(new InvalidCurrencyCodeException("Requested currency code '" + invalidCurrencyCode + "' is invalid, it must be a 3-letter uppercase string"))
            .when(currencyRequestServiceSpy).getCurrencyValue(invalidCurrencyCode);

    //then
    InvalidCurrencyCodeException expectedException = assertThrows(InvalidCurrencyCodeException.class,
            () -> currencyRequestServiceSpy.add(currencyRequest),
            "Expected InvalidCurrencyCodeException to be thrown with the specified message.");

    // verify if correct exception was thrown
    assertEquals("Requested currency code '" + invalidCurrencyCode + "' is invalid, it must be a 3-letter uppercase string", expectedException.getErrorMessage());

    verify(currencyRequestServiceSpy, times(1)).getCurrencyValue(invalidCurrencyCode);
    // make sure invalid request isn't saved in DB
    verify(currencyRequestRepository, times(0)).save(any());
  }

  @Test
  void testAddCurrencyNotFound() {
    String validCurrencyCode = "XYZ";
    CurrencyRequest currencyRequest = CurrencyRequest.builder().id(1L).currencyCode(validCurrencyCode).build();
    // Creating a spy to mock only one method
    CurrencyRequestService currencyRequestServiceSpy = spy(currencyRequestService);

    // mock exception throw on getCurrencyValue(String currencyCode) method
    doThrow(new CurrencyNotFoundException("Provided currency code: '" + validCurrencyCode + "' couldn't be found in NBP Web API."))
            .when(currencyRequestServiceSpy).getCurrencyValue(validCurrencyCode);

    //then
    CurrencyNotFoundException expectedException = assertThrows(CurrencyNotFoundException.class,
            () -> currencyRequestServiceSpy.add(currencyRequest),
            "Expected CurrencyNotFoundException to be thrown with the specified message.");

    // verify if correct exception was thrown
    assertEquals("Provided currency code: '" + validCurrencyCode + "' couldn't be found in NBP Web API.", expectedException.getErrorMessage());
    verify(currencyRequestServiceSpy, times(1)).getCurrencyValue(validCurrencyCode);
    // make sure invalid request isn't saved in DB
    verify(currencyRequestRepository, times(0)).save(any());
  }

  @Test
  void testAddUnhandledResponseException() {
    String validCurrencyCode = "XYZ";
    CurrencyRequest currencyRequest = CurrencyRequest.builder().id(1L).currencyCode(validCurrencyCode).build();
    // Creating a spy to mock only one method
    CurrencyRequestService currencyRequestServiceSpy = spy(currencyRequestService);

    // mock exception throw on getCurrencyValue(String currencyCode) method
    doThrow(UnhandledResponseException.class)
            .when(currencyRequestServiceSpy).getCurrencyValue(anyString());

    //then
    assertThrows(UnhandledResponseException.class,
            () -> currencyRequestServiceSpy.add(currencyRequest));

    verify(currencyRequestServiceSpy, times(1)).getCurrencyValue(validCurrencyCode);
    // make sure invalid request isn't saved in DB
    verify(currencyRequestRepository, times(0)).save(any());
  }
}