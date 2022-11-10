package cumiskey.ciaran.DockerATM;

import cumiskey.ciaran.DockerATM.apiobjects.*;
import cumiskey.ciaran.DockerATM.model.Customer;
import cumiskey.ciaran.DockerATM.model.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

@RestController
public class ATMController {

  @Autowired
  private final CustomerRepository customerRepository;

  @Autowired
  private TransactionRepository transactionRepository;

  private final AutomatedTellerMachine atm;

  private final Logger logger = LoggerFactory.getLogger(ATMController.class);

  private static final String AC_NOT_FOUND_MSG = "Account not found for this number.";
  private static final String INCORRECT_PIN_MSG = "Incorrect PIN entered.";
  private static final String INSUFFICIENT_FUNDS_MSG = "Insufficient funds for withdrawal.";

  ATMController(CustomerRepository customerRepository) {
    this.customerRepository = customerRepository;
    final TreeMap<Integer, Integer> initialFunds = new TreeMap<>();
    initialFunds.put(5, 20); // 20 x €5 notes
    initialFunds.put(10, 30); // 30 x €10 notes
    initialFunds.put(20, 30); // 30 x €20 notes
    initialFunds.put(50, 10); // 10 x €50 notes

    this.atm = new AutomatedTellerMachine(initialFunds);
  }

  //Shows the amount of cash and how many of each note are available.
  @GetMapping("/status")
  public BasicResponse getATMStatus() {
    return new ATMStatusResponse(this.atm);
  }

  @PostMapping("/balance")
  public BasicResponse getBalance(@RequestBody BalanceCheckRequest request) {
    final String requestedAccountNum = request.getAccountNumber();
    final Optional<Customer> optionalRequestedAccount = this.customerRepository.findById(requestedAccountNum);
    if(!optionalRequestedAccount.isPresent()) {
      logger.error(AC_NOT_FOUND_MSG);
      return new BasicResponse(ATMStatus.ACCOUNT_NOT_FOUND.getValue(), AC_NOT_FOUND_MSG);
    }
    final Customer requestedAccount = optionalRequestedAccount.get();
    //Verify the PIN
    if(!requestedAccount.getPin().equals(request.getAccountPIN())) {
      logger.error(INCORRECT_PIN_MSG);
      return new BasicResponse(ATMStatus.INCORRECT_PIN.getValue(), INCORRECT_PIN_MSG);
    }
    final BigDecimal balance = requestedAccount.getBalance();
    final int atmFunds = this.atm.getCashAvailable();
    final double withdrawalLimit = requestedAccount.getWithdrawalLimit();
    if(withdrawalLimit < atmFunds) {
      return new BalanceCheckResponse(balance, BigDecimal.valueOf(withdrawalLimit).setScale(2, RoundingMode.CEILING));
    }
    //If the user's balance is more than how much cash is in the ATM, they can only withdraw up to the amount inside.
    return new BalanceCheckResponse(balance, BigDecimal.valueOf(atmFunds));
  }

  @PostMapping("/withdraw")
  public BasicResponse withdrawMoney(@RequestBody WithdrawalRequest request) {
    final int withdrawalAmount = Integer.parseInt(request.getWithdrawalAmount());
    if(withdrawalAmount % 5 != 0) { //Since this ATM only dispenses Euro banknotes, it can only satisfy withdrawals that are a multiple of 5.
      logger.error("User tried to request a withdrawal that wasn't in multiples of €5.");
      return new BasicResponse(ATMStatus.ATM_CANNOT_FULFIL_WITHDRAWAL.getValue(), "Withdrawals can only be in multiples of €5.");
    }
    final String requestedAccountNum = request.getAccountNumber();
    final Optional<Customer> optionalRequestedAccount = this.customerRepository.findById(requestedAccountNum);
    if(!optionalRequestedAccount.isPresent()) {
      logger.error(AC_NOT_FOUND_MSG);
      return new BasicResponse(ATMStatus.ACCOUNT_NOT_FOUND.getValue(), AC_NOT_FOUND_MSG);
    }
    final Customer requestedAccount = optionalRequestedAccount.get();
    //Verify the PIN
    if(!requestedAccount.getPin().equals(request.getAccountPIN())) {
      logger.error(INCORRECT_PIN_MSG);
      return new BasicResponse(ATMStatus.INCORRECT_PIN.getValue(), "Incorrect PIN entered.");
    }
    if(requestedAccount.withdraw(withdrawalAmount)) { //will be true if the withdrawal was successful
      if (withdrawalAmount <= this.atm.getCashAvailable()) {
        final Map<Integer, Integer> withdrawnNotes = this.atm.withdrawCash(withdrawalAmount);
        if (withdrawnNotes.isEmpty()) {
          logger.error("Unable to fulfil withdrawal due to insufficient amounts of the right banknotes available to fulfil this request.");
          return new BasicResponse(ATMStatus.ATM_CANNOT_FULFIL_WITHDRAWAL.getValue(), "Unable to process withdrawal, as the right notes were unavailable.");
        }
        logger.info("€" + this.atm.getCashAvailable() + " left in the ATM");
        //Save the update to the account balance
        this.customerRepository.save(requestedAccount);
        final BigDecimal customerBalance = requestedAccount.getBalance().setScale(2, RoundingMode.CEILING);

        final Transaction transaction = new Transaction(requestedAccount, BigDecimal.valueOf(withdrawalAmount),
            "ATM Withdrawal");
        this.transactionRepository.save(transaction);

        return new WithdrawalResponse(withdrawnNotes, customerBalance);
      } else {
        logger.error("Unable to fulfil withdrawal of €" + withdrawalAmount + ", only " + this.atm.getCashAvailable() + " left in the machine.");
        return new BasicResponse(ATMStatus.ATM_CANNOT_FULFIL_WITHDRAWAL.getValue(),
            "Insufficient funds in ATM to process withdrawal.");
      }
    } else {
      return new BasicResponse(ATMStatus.INSUFFICIENT_FUNDS.getValue(), INSUFFICIENT_FUNDS_MSG);
    }
  }

  @PostMapping("/transfer")
  public BasicResponse transferMoney(@RequestBody TransferRequest request) {
    final String payerAccountId = request.getAccountNumber();
    if(!payerAccountId.equals(request.getPayeeAccount())) {
      final String INVALID_TRANSACTION = "Invalid transaction - payer and payee must be different accounts";
      logger.error(INVALID_TRANSACTION);
      return new BasicResponse(ATMStatus.INVALID_TRANSACTION.getValue(), INVALID_TRANSACTION);
    }
    final Optional<Customer> optionalPayerAccount = this.customerRepository.findById(payerAccountId);
    if(!optionalPayerAccount.isPresent()) {
      logger.error(AC_NOT_FOUND_MSG);
      return new BasicResponse(ATMStatus.ACCOUNT_NOT_FOUND.getValue(), AC_NOT_FOUND_MSG);
    }
    final Customer payerAccount = optionalPayerAccount.get();
    if(!payerAccount.getPin().equals(request.getAccountPIN())) {
      logger.error(INCORRECT_PIN_MSG);
      return new BasicResponse(ATMStatus.INCORRECT_PIN.getValue(), INCORRECT_PIN_MSG);
    }
    final Optional<Customer> optionalPayeeAccount = this.customerRepository.findById(request.getPayeeAccount());
    if(!optionalPayeeAccount.isPresent()) {
      final String PAYEE_NOT_FOUND = "Account not found for payee.";
      logger.error(PAYEE_NOT_FOUND);
      return new BasicResponse(ATMStatus.ACCOUNT_NOT_FOUND.getValue(), PAYEE_NOT_FOUND);
    }
    final BigDecimal transferAmount = BigDecimal.valueOf(Double.parseDouble(request.getTransferAmount()));
    if(payerAccount.withdraw(transferAmount)) {
      final Customer payeeAccount = optionalPayeeAccount.get();
      payeeAccount.deposit(transferAmount);
      this.customerRepository.save(payerAccount);
      this.customerRepository.save(payeeAccount);

      final Transaction payerTransaction = new Transaction(payerAccount, transferAmount,
              "Sent €" + transferAmount.toPlainString() + " to " + payeeAccount.getAccountNumber());
      final Transaction payeeTransaction = new Transaction(payerAccount, BigDecimal.ZERO.subtract(transferAmount),
              "Received €" + transferAmount.toPlainString() + " from " + payerAccountId);

      this.transactionRepository.save(payerTransaction);
      this.transactionRepository.save(payeeTransaction);
      return new TransferResponse(transferAmount, payerAccount.getBalance());
    } else {
      logger.error(INSUFFICIENT_FUNDS_MSG);
      return new BasicResponse(ATMStatus.INSUFFICIENT_FUNDS.getValue(), INSUFFICIENT_FUNDS_MSG);
    }
  }

  @PostMapping("/transactions")
  public BasicResponse showTransactions(@RequestBody BalanceCheckRequest request) {
    final String requestedAccountNum = request.getAccountNumber();
    final Optional<Customer> optionalRequestedAccount = this.customerRepository.findById(requestedAccountNum);
    if(!optionalRequestedAccount.isPresent()) {
      logger.error(AC_NOT_FOUND_MSG);
      return new BasicResponse(ATMStatus.ACCOUNT_NOT_FOUND.getValue(), AC_NOT_FOUND_MSG);
    }
    final Customer requestedAccount = optionalRequestedAccount.get();
    //Verify the PIN
    if(!requestedAccount.getPin().equals(request.getAccountPIN())) {
      logger.error(INCORRECT_PIN_MSG);
      return new BasicResponse(ATMStatus.INCORRECT_PIN.getValue(), INCORRECT_PIN_MSG);
    }
    final List<Transaction> transactions =
        this.transactionRepository.getTransactionsForCustomer(requestedAccountNum);
    return new ShowTransactionsResponse(transactions);
  }
}
