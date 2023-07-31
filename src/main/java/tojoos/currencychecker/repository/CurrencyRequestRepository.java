package tojoos.currencychecker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tojoos.currencychecker.pojo.CurrencyRequest;

@Repository
public interface CurrencyRequestRepository extends JpaRepository<CurrencyRequest, Long> {
}
