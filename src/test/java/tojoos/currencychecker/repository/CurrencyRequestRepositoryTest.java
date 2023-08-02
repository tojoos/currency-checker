package tojoos.currencychecker.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import tojoos.currencychecker.pojo.CurrencyRequest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class CurrencyRequestRepositoryTest {

  @Autowired
  CurrencyRequestRepository currencyRequestRepository;

  @Test
  @DirtiesContext
  void testSave() {
    CurrencyRequest task1 = new CurrencyRequest();
    currencyRequestRepository.save(task1);

    // check if id was generated automatically
    assertNotNull(task1.getId());
    assertEquals(1, currencyRequestRepository.findAll().size());
  }

  @Test
  @DirtiesContext
  void testFindAll() {
    CurrencyRequest task1 = CurrencyRequest.builder().build();
    CurrencyRequest task2 = CurrencyRequest.builder().build();
    CurrencyRequest task3 = CurrencyRequest.builder().build();

    currencyRequestRepository.save(task1);
    currencyRequestRepository.save(task2);
    currencyRequestRepository.save(task3);

    List<CurrencyRequest> currencyRequestsFound = currencyRequestRepository.findAll();

    assertEquals(3, currencyRequestsFound.size());
  }
}
