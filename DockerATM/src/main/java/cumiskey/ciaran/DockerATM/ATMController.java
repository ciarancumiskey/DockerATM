package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.apiobjects.BalanceCheckRequest;
import cumiskey.ciaran.DockerATM.apiobjects.BalanceCheckResponse;
import cumiskey.ciaran.DockerATM.apiobjects.BasicResponse;
import cumiskey.ciaran.DockerATM.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class ATMController {

  private final CustomerRepository repository;

  private final Logger logger = LoggerFactory.getLogger(ATMController.class);

  ATMController(CustomerRepository repository) {
    this.repository = repository;
  }

  @GetMapping("/all")
  public List<Customer> getAllCustomers() {
    final List<Customer> allAccounts = this.repository.findAll();
    return allAccounts;
  }

  @GetMapping("/balance")
  public String testGet() {
    return "Hello there";
  }

  @PostMapping("/balance")
  public BasicResponse getBalance(@RequestBody BalanceCheckRequest request) {
    final Long requestedAccountNum = Long.parseLong(request.getAccountNumber());
    final Optional<Customer> optionalRequestedAccount = this.repository.findById(requestedAccountNum);
    if(!optionalRequestedAccount.isPresent()) {
      logger.error("Account not found");
      return new BasicResponse(HttpStatus.NOT_FOUND.value(), "No account found for this number.");
    }
    final Customer requestedAccount = optionalRequestedAccount.get();
    //Verify the PIN
    if(!requestedAccount.getPin().equals(request.getAccountPIN())) {
      logger.error("Wrong PIN entered");
      return new BasicResponse(HttpStatus.UNAUTHORIZED.value(), "Incorrect PIN entered.");
    }
    final int balance = requestedAccount.getBalance();
    final int withdrawalLimit = balance + requestedAccount.getOverdraft();
    return new BalanceCheckResponse(balance, withdrawalLimit);
  }
}
