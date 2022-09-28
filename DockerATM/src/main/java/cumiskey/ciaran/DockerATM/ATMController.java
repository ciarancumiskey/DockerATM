package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.apiobjects.BalanceCheckRequest;
import cumiskey.ciaran.DockerATM.apiobjects.BalanceCheckResponse;
import cumiskey.ciaran.DockerATM.apiobjects.BasicResponse;
import cumiskey.ciaran.DockerATM.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.TreeMap;

@RestController
public class ATMController {

  private final CustomerRepository repository;
  private final AutomatedTellerMachine atm;

  private final Logger logger = LoggerFactory.getLogger(ATMController.class);

  ATMController(CustomerRepository repository) {
    this.repository = repository;
    final TreeMap<Integer, Integer> initialFunds = new TreeMap<>();
    initialFunds.put(5, 20); // 20 x €5 notes
    initialFunds.put(10, 30); // 30 x €10 notes
    initialFunds.put(20, 30); // 30 x €20 notes
    initialFunds.put(50, 10); // 10 x €50 notes

    this.atm = new AutomatedTellerMachine(initialFunds);
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
    final int atmFunds = this.atm.getCashAvailable();
    final int withdrawalLimit = balance + requestedAccount.getOverdraft();
    if(withdrawalLimit < atmFunds) {
      return new BalanceCheckResponse(balance, withdrawalLimit);
    }
    //If the user's balance is more than how much cash is in the ATM, they can only withdraw up to the amount inside.
    return new BalanceCheckResponse(balance, atmFunds);
  }
}
