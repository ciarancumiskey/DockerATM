package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.apiobjects.*;
import cumiskey.ciaran.DockerATM.model.Customer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
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

  @PostMapping("/withdraw")
  public BasicResponse withdrawMoney(@RequestBody WithdrawalRequest request) {
    final int withdrawalAmount = Integer.parseInt(request.getWithdrawalAmount());
    if(withdrawalAmount % 5 != 0) { //Since this ATM only dispenses Euro banknotes, it can only satisfy withdrawals that are a multiple of 5.
      logger.error("User tried to request a withdrawal that wasn't in multiples of €5.");
      return new BasicResponse(ATMStatus.ATM_CANNOT_FULFIL_WITHDRAWAL.getStatusId(), "Withdrawals can only be in multiples of €5.");
    }
    final Long requestedAccountNum = Long.parseLong(request.getAccountNumber());
    final Optional<Customer> optionalRequestedAccount = this.repository.findById(requestedAccountNum);
    if(!optionalRequestedAccount.isPresent()) {
      logger.error("Account not found");
      return new BasicResponse(ATMStatus.ACCOUNT_NOT_FOUND.getStatusId(), "No account found for this number.");
    }
    final Customer requestedAccount = optionalRequestedAccount.get();
    //Verify the PIN
    if(!requestedAccount.getPin().equals(request.getAccountPIN())) {
      logger.error("Wrong PIN entered");
      return new BasicResponse(ATMStatus.INCORRECT_PIN.getStatusId(), "Incorrect PIN entered.");
    }
    if(requestedAccount.withdraw(withdrawalAmount)) { //will be true if the withdrawal was successful
      if (withdrawalAmount <= this.atm.getCashAvailable()) {
        final Map<Integer, Integer> withdrawnNotes = this.atm.withdrawCash(withdrawalAmount);
        if (withdrawnNotes.isEmpty()) {
          logger.error("Unable to fulfil withdrawal due to insufficient amounts of the right banknotes available to fulfil this request.");
          return new BasicResponse(ATMStatus.ATM_CANNOT_FULFIL_WITHDRAWAL.getStatusId(), "Unable to process withdrawal, as the right notes were unavailable.");
        }
        logger.info("€" + this.atm.getCashAvailable() + " left in the ATM");
        //Save the update to the account balance
        this.repository.save(requestedAccount);
        final int customerBalance = requestedAccount.getBalance();
        return new WithdrawalResponse(withdrawnNotes, customerBalance);
      } else {
        logger.error("Unable to fulfil withdrawal of €" + withdrawalAmount + ", only " + this.atm.getCashAvailable() + " left in the machine.");
        return new BasicResponse(ATMStatus.ATM_CANNOT_FULFIL_WITHDRAWAL.getStatusId(),
            "Insufficient funds in ATM to process withdrawal.");
      }
    } else {
      return new BasicResponse(ATMStatus.INSUFFICIENT_FUNDS.getStatusId(), "Insufficient funds for withdrawal.");
    }
  }
}
