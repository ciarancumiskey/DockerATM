package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

  @Query(value = "SELECT t from Transaction t where t.customer = :customer")
  List<Transaction> getTransactionsForCustomer(@Param("customer") String accountNumber);
}
